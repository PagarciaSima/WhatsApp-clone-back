package com.pgs.whatsappclone.chat;

import org.springframework.stereotype.Service;

@Service
public class ChatMapper {
	
	/**
	 * Converts a {@link Chat} entity into a {@link ChatResponse} DTO, enriching it with dynamic
	 * information such as unread message count, last message content, and online status of the recipient.
	 *
	 * @param chat the chat entity to convert
	 * @param senderId the ID of the current user (used to personalize the response)
	 * @return a {@link ChatResponse} containing the chat information suitable for frontend consumption
	 */
	public ChatResponse toChatResponse(Chat chat, String senderId) {
		return ChatResponse.builder()
				.id(chat.getId())
				.name(chat.getChatName(senderId))
				.unreadCount(chat.getUnreadMessages(senderId))
				.lastMessage(chat.getLastMessage())
				.isRecipientOnline(chat.getRecipient().isUserOnline())
				.senderId(chat.getSender().getId())
				.receiverId(chat.getRecipient().getId())
				.build();
	}

}
