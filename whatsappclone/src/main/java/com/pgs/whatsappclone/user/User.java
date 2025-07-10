package com.pgs.whatsappclone.user;

import java.time.LocalDateTime;

import com.pgs.whatsappclone.common.BaseAuditingEntity;

import jakarta.persistence.Entity;
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
@Table(name = "users")
public class User extends BaseAuditingEntity {
	
	@Id
	private String id;
	
	private String firstName;
	
	private String lastName;
	
	private String email;
	
	// When the user was last seen (online or not)
	private LocalDateTime lastSeen;
}
