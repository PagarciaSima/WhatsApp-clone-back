package com.pgs.whatsappclone.chat;

import static jakarta.persistence.GenerationType.UUID;

import java.time.LocalDateTime;
import java.util.List;

import com.pgs.whatsappclone.common.BaseAuditingEntity;
import com.pgs.whatsappclone.message.Message;
import com.pgs.whatsappclone.message.MessageState;
import com.pgs.whatsappclone.message.MessageType;
import com.pgs.whatsappclone.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
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
@Table(name = "chat")
public class Chat extends BaseAuditingEntity{
	
	@Id
	@GeneratedValue(strategy = UUID)
	private String id;
	
	@ManyToOne
	@JoinColumn(name = "sender_id")
	private User sender;
	
	@ManyToOne
	@JoinColumn(name = "recipient_Id")
	private User recipient;
	
	@OneToMany(mappedBy = "chat", fetch = FetchType.EAGER)
	@OrderBy("createdDate DESC")
	private List<Message> messages;
	
	@Transient
	/**
	 * Returns the full name of the other user participating in the chat.
	 * <p>
	 * If the provided {@code currentUserId} matches the recipient's ID, it means the current user
	 * is the recipient, so the chat is named using the sender's information.
	 * Otherwise, the current user is the sender, and the chat is named using the recipient's information.
	 * </p>
	 *
	 * @param currentUserId the ID of the user requesting the chat name (typically the logged-in user)
	 * @return the full name (first + last name) of the other participant in the chat
	 */
	public String getChatName(final String currentUserId) {
		// The user is the recipient so chat is named with the sender data
		if(recipient.getId().equals(currentUserId)) {
			return sender.getFirstName() + " " + sender.getLastName();
		}
		// The user is the sender
		return recipient.getFirstName() +  " " + recipient.getLastName();
	}
	
	@Transient
	/**
	 * Calculates the number of unread messages for the specified user.
	 * <p>
	 * A message is considered unread if:
	 * <ul>
	 *   <li>The given user is the receiver of the message.</li>
	 *   <li>The message state is {@code SENT} (i.e., not yet seen).</li>
	 * </ul>
	 *
	 * @param currentUserId the ID of the user for whom to count unread messages
	 * @return the number of unread messages directed to the user
	 */
	public long getUnreadMessages(final String currentUserId) {
		return messages
				.stream()
				.filter(m -> m.getReceiverId().equals(currentUserId))
				.filter(m -> MessageState.SENT == m.getState())
				.count();
	}
	
	@Transient
	/**
	 * Returns the content of the last message in the chat.
	 * <p>
	 * If the last message is not of type TEXT (e.g., audio or video), 
	 * it returns the string "Attachment" instead.
	 * Messages are expected to be ordered by creation date in descending order,
	 * so the most recent message is at index 0.
	 * <p>
	 * If there are no messages, returns {@code null}.
	 *
	 * @return the content of the last message or "Attachment" if it is not a text message, or {@code null} if no messages exist
	 */
	public String getLastMessage() {
		if(messages != null && !messages.isEmpty()) {
			// Messages are ordered by created date -> 0 is the most recent
			if(messages.get(0).getType() != MessageType.TEXT) {
				// Audio or video
				return "Attachment";
			}
			return messages.get(0).getContent();
		}
		return null;
	}
	
	@Transient
	public LocalDateTime getLastMessageTime() {
		if(messages != null && !messages.isEmpty()) {
			return messages.get(0).getCreatedDate();
		}
		return null;
	}
}
