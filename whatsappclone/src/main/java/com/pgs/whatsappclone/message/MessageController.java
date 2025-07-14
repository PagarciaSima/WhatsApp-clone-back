package com.pgs.whatsappclone.message;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@Slf4j
public class MessageController {

	private final MessageService messageService;
	
	/**
	 * Handles the HTTP POST request to save a new message.
	 *
	 * @param message the message request payload containing message details
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void saveMessage(@RequestBody MessageRequest message) {
	    log.info("Received request to save message for chat ID: {}", message.getChatId());
	    this.messageService.savedMessage(message);
	    log.info("Message saved successfully for chat ID: {}", message.getChatId());
	}
	
	/**
	 * Handles uploading a media file for a given chat.
	 *
	 * @param chatId the ID of the chat where the media message will be sent
	 * @param file the media file to upload
	 * @param authentication the current authenticated user
	 * @throws RuntimeException if any error occurs during file upload or message saving
	 */
	@PostMapping(value = "/upload-media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public void uploadMedia(
	    @RequestParam("chat-id") String chatId,
	    // TODO add param from swagger
	    @RequestParam("file") MultipartFile file,
	    Authentication authentication
	) {
	    log.info("Received request to upload media file for chat ID: {}", chatId);
	    
	    try {
	        this.messageService.uploadMediaMessage(chatId, file, authentication);
	        log.info("Media file uploaded successfully for chat ID: {}", chatId);
	    } catch (Exception e) {
	        log.error("Error uploading media file for chat ID: {}", chatId, e);
	        throw e; // Re-lanzar para que el controlador maneje la excepción (o customize)
	    }
	}
	
	/**
	 * Marks all messages in the specified chat as "seen" by the authenticated user.
	 *
	 * @param chatId the ID of the chat whose messages should be marked as seen
	 * @param authentication the current authenticated user
	 */
	@PatchMapping
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void setMessagesToSeen(@RequestParam("chat-id") String chatId, Authentication authentication) {
	    log.info("Request received to mark messages as SEEN for chat ID: {}", chatId);
	    this.messageService.setMessagesToSeen(chatId, authentication);
	    log.info("Messages marked as SEEN for chat ID: {}", chatId);
	}
	
	/**
	 * Retrieves all messages associated with a given chat ID.
	 *
	 * @param chatId the ID of the chat whose messages are to be retrieved
	 * @return a ResponseEntity containing the list of MessageResponse objects and an HTTP 200 status
	 */
	@GetMapping("/chat/{chat-id}")
	public ResponseEntity<List<MessageResponse>> getMessages(@PathVariable("chat-id") String chatId) {
	    log.info("Received request to fetch messages for chat ID: {}", chatId);

	    List<MessageResponse> messages = this.messageService.findChatMessages(chatId);

	    log.info("Returning {} messages for chat ID: {}", messages.size(), chatId);
	    return ResponseEntity.ok(messages);
	}
}
