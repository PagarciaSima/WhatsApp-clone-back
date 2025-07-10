package com.pgs.whatsappclone.user;

import java.time.LocalDateTime;
import java.util.List;

import com.pgs.whatsappclone.chat.Chat;
import com.pgs.whatsappclone.common.BaseAuditingEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
	
	private static final int LAST_ACTIVE_INTERVAL = 5;

	@Id
	private String id;
	
	private String firstName;
	
	private String lastName;
	
	private String email;
	
	// When the user was last seen (online or not)
	private LocalDateTime lastSeen;
	
	@OneToMany(mappedBy = "sender")
	private List<Chat> chatAsSender;
	
	@OneToMany(mappedBy = "recipient")
	private List<Chat> chatAsRecipient;
	
	@Transient
	/**
	 * Determines whether the user is currently considered online.
	 * <p>
	 * A user is considered online if their {@code lastSeen} timestamp
	 * is within the last {@value #LAST_ACTIVE_INTERVAL} minutes from the current time.
	 * </p>
	 *
	 * @return {@code true} if the user has been active within the last {@value #LAST_ACTIVE_INTERVAL} minutes; {@code false} otherwise.
	 */
	public boolean isUserOnline() {
		// lastSeen = 10:05
		// now 10:09 --> online (<5 min)
		// now 10:12 --> offline (<5 min)
		return lastSeen != null && lastSeen.isAfter(LocalDateTime.now().minusMinutes(LAST_ACTIVE_INTERVAL));
	}

}
