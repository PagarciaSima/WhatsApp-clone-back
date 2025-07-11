package com.pgs.whatsappclone.user;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class UserMapper {
	
	/**
	 * Creates a {@link User} object from the given JWT token attributes.
	 * <p>
	 * This method extracts standard identity claims such as subject (ID), first name,
	 * last name, and email from the token and maps them to a new {@code User} instance.
	 * It also sets the current timestamp as the user's last seen time.
	 * </p>
	 *
	 * <ul>
	 *   <li>{@code sub} → sets the user's ID.</li>
	 *   <li>{@code given_name} or {@code nickname} → sets the user's first name (prefers {@code given_name}).</li>
	 *   <li>{@code family_name} → sets the user's last name.</li>
	 *   <li>{@code email} → sets the user's email address.</li>
	 * </ul>
	 *
	 * @param attributes a map containing JWT token claims
	 * @return a new {@code User} instance populated with values from the token
	 */
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
