package com.pgs.whatsappclone.notification;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

	private final SimpMessagingTemplate messagingTemplate;
	
	/**
	 * Sends a WebSocket notification to a specific user.
	 *
	 * <p>This method uses Spring's {@link org.springframework.messaging.simp.SimpMessagingTemplate}
	 * to send a message to a user-specific destination (e.g., "/user/{userId}/chat").</p>
	 *
	 * @param userId       the ID of the user to whom the notification will be sent
	 * @param notification the notification payload to send
	 */
	public void sendNotification(String userId, Notification notification) {
		log.info("Sending WS notification to {} with payload {}", userId, notification);
		this.messagingTemplate.convertAndSendToUser(
				userId, // User
				"/chat", // Destination
				notification); // Object
	}
}
