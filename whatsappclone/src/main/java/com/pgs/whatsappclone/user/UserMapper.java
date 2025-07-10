package com.pgs.whatsappclone.user;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class UserMapper {

	public User fromTokenAttributes(Map<String, Object> attributes) {
		User user = new User();
		// Subject / identifier
		if (attributes.containsKey("sub")) {
			user.setId(attributes.get("sub").toString());
		}

		// First name can be given name or nickname
		if (attributes.containsKey("given_name")) {
			user.setFirstName(attributes.get("given_name").toString());
		}

		else if (attributes.containsKey("nickname")) {
			user.setFirstName(attributes.get("nickname").toString());
		}
		// Surname
		if (attributes.containsKey("family_name")) {
			user.setLastName(attributes.get("family_name").toString());

		}
		// Email
		if (attributes.containsKey("email")) {
			user.setEmail(attributes.get("email").toString());
		}
		
		user.setLastSeen(LocalDateTime.now());
		return user;
	}

}
