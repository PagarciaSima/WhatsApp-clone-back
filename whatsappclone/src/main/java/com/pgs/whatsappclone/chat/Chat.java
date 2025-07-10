package com.pgs.whatsappclone.chat;

import static jakarta.persistence.GenerationType.UUID;

import java.util.List;

import com.pgs.whatsappclone.common.BaseAuditingEntity;
import com.pgs.whatsappclone.message.Message;
import com.pgs.whatsappclone.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
@Table(name = "chat")
public class Chat extends BaseAuditingEntity{
	
	@Id
	@GeneratedValue(strategy = UUID)
	private String id;
	
	private User sender;
	
	private User recipient;
	
	private List<Message> messages;
}
