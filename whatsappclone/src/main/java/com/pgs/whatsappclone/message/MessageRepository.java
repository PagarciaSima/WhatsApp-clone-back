package com.pgs.whatsappclone.message;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long>{
	
	// Linked to namedQuery in model
	@Query(name = MessageConstants.FIND_MESSAGES_BY_CHAT_ID)
	List<Message> findMessagesByChatId(String chatId);
	
	@Query(name = MessageConstants.SET_MESSAGES_TO_SEEN_BY_CHAT)
	@Modifying // Writing operation
	void setMessagesToSeenByChatId(@Param("chatId") String chatId,@Param("newState") MessageState state);

}
