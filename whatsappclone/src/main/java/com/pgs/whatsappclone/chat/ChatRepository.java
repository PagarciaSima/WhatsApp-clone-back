package com.pgs.whatsappclone.chat;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRepository extends JpaRepository<Chat, String>{

	// Linked automatically to the namedQuery in the model (the param was named senderId
	@Query(name = ChatConstants.FIND_CHAT_BY_SENDER_ID)
	List<Chat> findBySenderId(@Param ("senderId") String userId);

	@Query(name = ChatConstants.FIND_CHAT_BY_SENDER_ID_AND_RECEIVER)
	Optional<Chat> findChatByReceiverAndSender(@Param("senderId") String senderId,@Param("recipientId") String receiverId);

}
