package com.pgs.whatsappclone.notification;

import com.pgs.whatsappclone.message.MessageType;

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
public class Notification {

	private String chatId;
	private String content;
	private String receiverId;
	private String senderId;
	private String chatName;
	private MessageType messageType;
	private NotificationType type;
	private byte [] media;
}
