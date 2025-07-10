package com.pgs.whatsappclone.message;

import com.pgs.whatsappclone.chat.Chat;
import com.pgs.whatsappclone.common.BaseAuditingEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "messages")
public class Message extends BaseAuditingEntity{
	
	@Id
	@SequenceGenerator(name = "msg_seq", sequenceName = "msg_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "msg_seq")
	private Long id;
	
	@Column(columnDefinition = "TEXT")
	private String content;
	
	@Enumerated(EnumType.STRING)
	private MessageState state;
	
	@Enumerated(EnumType.STRING)
	private MessageType type;
	
	@ManyToOne
	@JoinColumn(name = "chat_id")
	private Chat chat;
	
	@Column(name = "sender_id", nullable = false)
	private String senderId;
	
	@Column(name = "receiver_id", nullable = false)
	private String receiverId;
}
