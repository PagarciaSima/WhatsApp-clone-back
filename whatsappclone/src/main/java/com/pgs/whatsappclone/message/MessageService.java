package com.pgs.whatsappclone.message;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.pgs.whatsappclone.chat.Chat;
import com.pgs.whatsappclone.chat.ChatRepository;
import com.pgs.whatsappclone.file.FileService;
import com.pgs.whatsappclone.file.FileUtils;
import com.pgs.whatsappclone.notification.Notification;
import com.pgs.whatsappclone.notification.NotificationService;
import com.pgs.whatsappclone.notification.NotificationType;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

	private final MessageRepository messageRepository;
	private final ChatRepository chatRepository;
	private final MessageMapper mapper;
	private final FileService fileService;
	private final NotificationService notificationService;

	/**
	 * Saves a new message in the database associated with a chat.
	 * 
	 * @param messageRequest the request object containing message details including
	 *                       chat ID, sender ID, receiver ID, content, and message
	 *                       type.
	 * @throws EntityNotFoundException if the chat with the specified ID does not
	 *                                 exist.
	 */
	@Transactional
	public void savedMessage(MessageRequest messageRequest) {
		log.info("Saving message to chat with ID {}", messageRequest.getChatId());

		// Look for the associated chat
		Chat chat = this.chatRepository.findById(messageRequest.getChatId()).orElseThrow(() -> {
			log.error("Chat not found, ID: {}", messageRequest.getChatId());
			return new EntityNotFoundException("Chat not found, ID: " + messageRequest.getChatId());
		});
		// Map message
		Message message = new Message();
		message.setContent(messageRequest.getContent());
		message.setChat(chat);
		message.setSenderId(messageRequest.getSenderId());
		message.setReceiverId(messageRequest.getReceiverId());
		message.setType(messageRequest.getType());
		// SENT state by default
		message.setState(MessageState.SENT);

		this.messageRepository.save(message);
		
		// Build notification
		Notification notification = Notification.builder()
				.chatId(chat.getId())
				.messageType(messageRequest.getType())
				.content(messageRequest.getContent())
				.senderId(messageRequest.getSenderId())
				.receiverId(messageRequest.getReceiverId())
				.type(NotificationType.MESSAGE)
				.chatName(chat.getTargetChatName(message.getSenderId()))
				.build();

		log.info("Built notification to be sent to user {}: {}", message.getReceiverId(), notification);

		// Send notification
		this.notificationService.sendNotification(message.getReceiverId(), notification);

		log.info("Message saved successfully with sender ID {} and receiver ID {}", messageRequest.getSenderId(),
				messageRequest.getReceiverId());
	}

	/**
	 * Retrieves all messages for a given chat ID and maps them to MessageResponse
	 * DTOs.
	 * 
	 * @param chatId the ID of the chat whose messages are to be retrieved
	 * @return a list of MessageResponse objects representing the messages of the
	 *         chat
	 */
	@Transactional(readOnly = true)
	public List<MessageResponse> findChatMessages(String chatId) {
		log.info("Fetching messages for chat ID {}", chatId);

		List<MessageResponse> messages = this.messageRepository.findMessagesByChatId(chatId).stream()
				.map(this.mapper::toMessageResponse).toList();

		log.info("Found {} messages for chat ID {}", messages.size(), chatId);
		return messages;
	}
	
	/**
	 * Marks all messages in a given chat as "seen" for the recipient.
	 *
	 * <p>This method retrieves the chat by its ID, determines the recipient based on the
	 * authenticated user, and updates the message state to {@code SEEN} for that chat.</p>
	 *
	 * <p>The operation is executed within a transactional context to ensure consistency.</p>
	 *
	 * @param chatId the ID of the chat whose messages should be marked as seen
	 * @param authentication the current authenticated user used to determine the recipient
	 * @throws EntityNotFoundException if the chat with the given ID is not found
	 */
	@Transactional
	public void setMessagesToSeen(String chatId, Authentication authentication) {
	    log.info("Setting messages to SEEN for chat ID: {}", chatId);

	    Chat chat = this.chatRepository.findById(chatId)
	            .orElseThrow(() -> {
	                log.error("Chat not found, ID: {}", chatId);
	                return new EntityNotFoundException("Chat not found, ID: " + chatId);
	            });

	    final String recipientId = this.getRecipientId(chat, authentication);
	    log.debug("Authenticated user is considered recipient with ID: {}", recipientId);

	    this.messageRepository.setMessagesToSeenByChatId(chatId, MessageState.SEEN);

	    log.info("Messages set to SEEN for chat ID: {} and recipient ID: {}", chatId, recipientId);

	    // Build notification
		Notification notification = Notification.builder()
				.chatId(chat.getId())
				.type(NotificationType.MESSAGE)
				.receiverId(getSenderId(chat, authentication))
				.senderId(recipientId)
				.build();

		log.info("Built notification to be sent to user {}: {}", recipientId, notification);

		// Send notification
		this.notificationService.sendNotification(recipientId, notification);
	}
	
	/**
	 * Handles uploading a media file as a message within a chat.
	 * 
	 * <p>This method saves the uploaded file via the FileService, creates a new message
	 * associated with the chat, sets the message type to IMAGE, and saves it to the database.</p>
	 * 
	 * @param chatId the ID of the chat to which the media message belongs
	 * @param file the media file uploaded
	 * @param authentication the current authenticated user, used to determine sender and recipient
	 * @throws EntityNotFoundException if the chat with the given ID does not exist
	 */
	public void uploadMediaMessage(String chatId, MultipartFile file, Authentication authentication) {
	    log.info("Uploading media message to chat ID: {}", chatId);

	    Chat chat = this.chatRepository.findById(chatId)
	        .orElseThrow(() -> {
	            log.error("Chat not found, ID: {}", chatId);
	            return new EntityNotFoundException("Chat not found, ID: " + chatId);
	        });

	    final String senderId = this.getSenderId(chat, authentication);
	    final String recipientId = this.getRecipientId(chat, authentication);
	    log.debug("Determined sender ID: {} and recipient ID: {}", senderId, recipientId);

	    final String filePath = this.fileService.saveFile(file, senderId);
	    log.info("File saved at path: {}", filePath);

	    Message message = new Message();
	    message.setChat(chat);
	    message.setSenderId(senderId);
	    message.setReceiverId(recipientId);
	    message.setType(MessageType.IMAGE);
	    message.setMediaFilePath(filePath);
	    // SENT state by default
	    message.setState(MessageState.SENT);

	    this.messageRepository.save(message);
	    log.info("Media message saved successfully for chat ID: {} from sender ID: {} to recipient ID: {}", chatId, senderId, recipientId);
	    
	    // Build notification
		Notification notification = Notification.builder()
				.chatId(chat.getId())
				.type(NotificationType.IMAGE)
				.messageType(MessageType.IMAGE)
				.senderId(senderId)
				.receiverId(recipientId)
				.media(FileUtils.readFileFromLocation(filePath))
				.build();

		log.info("Built notification to be sent to user {}: {}", recipientId, notification);

		// Send notification
		this.notificationService.sendNotification(recipientId, notification);
	}
	
	/**
	 * Returns the sender ID of the chat relative to the authenticated user.
	 * If the authenticated user is the sender, returns the sender's ID;
	 * otherwise, returns the recipient's ID.
	 *
	 * @param chat the chat object containing sender and recipient information
	 * @param authentication the authentication object representing the current user
	 * @return the ID of the sender if the user is the sender, or the recipient's ID otherwise
	 */
	private String getSenderId(Chat chat, Authentication authentication) {
	    String authUserId = authentication.getName();
	    String senderId = chat.getSender().getId();
	    String recipientId = chat.getRecipient().getId();

	    log.info("Authenticated user ID: {}", authUserId);
	    log.info("Chat sender ID: {}, Chat recipient ID: {}", senderId, recipientId);

	    if (senderId.equals(authUserId)) {
	        log.info("User is the sender. Returning sender ID: {}", senderId);
	        return senderId;
	    }
	    
	    log.info("User is not the sender. Returning recipient ID: {}", recipientId);
	    return recipientId;
	}

	/**
	 * Returns the recipient ID of the chat relative to the authenticated user.
	 * If the authenticated user is the sender, returns the recipient's ID;
	 * otherwise, returns the sender's ID.
	 *
	 * @param chat the chat object containing sender and recipient information
	 * @param authentication the authentication object representing the current user
	 * @return the ID of the recipient relative to the authenticated user
	 */
	private String getRecipientId(Chat chat, Authentication authentication) {
	    String authUserId = authentication.getName();
	    String senderId = chat.getSender().getId();
	    String recipientId = chat.getRecipient().getId();

	    log.info("Authenticated user ID: {}", authUserId);
	    log.info("Chat sender ID: {}, Chat recipient ID: {}", senderId, recipientId);

	    if (senderId.equals(authUserId)) {
	        log.info("User is the sender. Returning recipient ID: {}", recipientId);
	        return recipientId;
	    }

	    log.info("User is not the sender. Returning sender ID: {}", senderId);
	    return senderId;
	}
}
