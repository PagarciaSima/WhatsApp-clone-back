package com.pgs.whatsappclone.chat;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pgs.whatsappclone.common.StringResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
	
	private final ChatService chatService;
	
	 /**
     * Creates a new chat between the sender and receiver users.
     * <p>
     * If a chat between the specified users already exists, it returns the existing chat ID.
     * Otherwise, a new chat is created and its ID is returned.
     * </p>
     *
     * @param senderId   the ID of the user initiating the chat
     * @param receiverId the ID of the user receiving the chat
     * @return a ResponseEntity containing the chat ID in a StringResponse object
     */
    @PostMapping
    public ResponseEntity<StringResponse> createChat(
            @RequestParam(name = "sender-id") String senderId,
            @RequestParam(name = "receiver-id") String receiverId
    ) {
        log.info("Creating chat between sender: {} and receiver: {}", senderId, receiverId);

        final String chatId = this.chatService.createChat(senderId, receiverId);

        log.debug("Chat created with ID: {}", chatId);

        StringResponse response = StringResponse.builder()
                .response(chatId)
                .build();

        return ResponseEntity.ok(response);
    }
    
    /**
     * Handles a GET request to retrieve all chats for the currently authenticated user.
     *
     * @param authentication the authentication object containing the currently authenticated user's details
     * @return a {@link ResponseEntity} containing a list of {@link ChatResponse} objects representing the user's chats
     */
    @GetMapping
    public ResponseEntity<List<ChatResponse>> getChatsByReceiver(Authentication authenticcation) {
    	return ResponseEntity.ok(this.chatService.getChatsByReceiverId(authenticcation));
    }
}
