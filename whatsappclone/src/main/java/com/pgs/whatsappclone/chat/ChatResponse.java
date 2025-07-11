package com.pgs.whatsappclone.chat;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {

	private String id;
	private String name;
	private long unreadCount;
	private String lastMessage;
	private LocalDateTime lastMessageTime;
	private boolean isRecipientOnline;
	private String senderId;
	private String receiverId;
}
