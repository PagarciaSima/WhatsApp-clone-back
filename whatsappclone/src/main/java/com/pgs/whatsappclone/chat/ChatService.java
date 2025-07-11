package com.pgs.whatsappclone.chat;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pgs.whatsappclone.user.User;
import com.pgs.whatsappclone.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
	
	private final ChatRepository chatRepository;
	private final UserRepository userRepository;
	private final ChatMapper mapper;
	
	@Transactional(readOnly = true)
	/**
	 * Retrieves a list of chat responses for the current authenticated user as the sender.
	 * <p>
	 * This method queries the repository for all chats where the current user is the sender,
	 * then maps each {@link Chat} entity to a {@link ChatResponse} DTO using the provided mapper.
	 * The result is a list of chat responses relevant to the user.
	 * </p>
	 *
	 * @param currentUser the current authenticated user
	 * @return a list of {@link ChatResponse} objects representing the user's chats
	 */
	public List<ChatResponse> getChatsByReceiverId(Authentication currentUser) {
		final String userId = currentUser.getName();
		return this.chatRepository.findBySenderId(userId)
				.stream()
				.map(c -> this.mapper.toChatResponse(c, userId))
				.toList();
	}
	
	/**
	 * Creates a new chat between two users if it does not already exist.
	 * <p>
	 * First, the method checks whether a chat already exists between the specified sender and receiver.
	 * If such a chat exists, its ID is returned. Otherwise, both users are retrieved from the database
	 * using their public IDs. If either user does not exist, an {@link EntityNotFoundException} is thrown.
	 * A new {@link Chat} is then created and saved to the database, and its generated ID is returned.
	 * </p>
	 *
	 * @param senderId   the public ID of the user initiating the chat
	 * @param receiverId the public ID of the user receiving the chat
	 * @return the ID of the existing or newly created chat
	 * @throws EntityNotFoundException if either the sender or receiver user is not found in the database
	 */
	@Transactional
	public String createChat(String senderId, String receiverId) {
		log.info("Attempting to create chat between senderId={} and receiverId={}", senderId, receiverId);
		// First check if there is an already existing chat between the two members
		Optional<Chat> existingChat = this.chatRepository.findChatByReceiverAndSender(senderId, receiverId);
		if(existingChat.isPresent()) {
			return existingChat.get().getId();
		}
		// Find both users by public ID
		User sender = this.userRepository.findByPublicId(senderId)
				.orElseThrow(() -> {
					log.warn("Sender with ID {} not found", senderId);
					return new EntityNotFoundException("User not found, ID: " + senderId);
				});
		User receiver = this.userRepository.findByPublicId(receiverId)
				.orElseThrow(() -> {
					log.warn("Receiver with ID {} not found", receiverId);
					return new EntityNotFoundException("User not found, ID: " + receiverId);
				});
		// Implement the chat
		Chat chat = new Chat();
		chat.setSender(sender);
		chat.setRecipient(receiver);
		
		Chat savedChat = this.chatRepository.save(chat);
		log.info("Chat successfully created with ID: {}", savedChat.getId());

		return savedChat.getId();	
	}
}
