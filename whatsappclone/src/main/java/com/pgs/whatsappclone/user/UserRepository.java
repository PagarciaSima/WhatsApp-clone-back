package com.pgs.whatsappclone.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, String>{
	
	// Automatically asociated with queryname in User model
	@Query(name = UserConstants.FIND_USER_BY_EMAIL)
	Optional<User> findByEmail(@Param("email") String userEmail);

}
