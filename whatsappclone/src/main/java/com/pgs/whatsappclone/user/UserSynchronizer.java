package com.pgs.whatsappclone.user;

import java.util.Map;
import java.util.Optional;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSynchronizer {
	
	private final UserRepository userRepository;
	private final UserMapper userMapper;
	
	/**
	 * Synchronizes the user data with the identity provider (IDP) based on the given JWT token.
	 * <p>
	 * This method extracts the user's email from the token and attempts to find a matching
	 * user in the database. If a user with the same email exists, it reuses the existing user's ID
	 * to ensure the database entry is updated rather than creating a new one.
	 * <br>
	 * A new {@link User} entity is then built from the token attributes and saved to the repository.
	 * </p>
	 *
	 * @param token the JWT token containing user identity claims from the IDP
	 */
	public void synchronizeWithIdp(Jwt token) {
		log.info("Synchronizing user with idp");
		this.getUserEmail(token).ifPresent(userEmail -> {
			log.info("Synchronizing user having email {}", userEmail);
			Optional<User> optUser = this.userRepository.findByEmail(userEmail);
			User user = this.userMapper.fromTokenAttributes(token.getClaims());
			// Update by id if it matches
			optUser.ifPresent(value -> user.setId(optUser.get().getId()));
			
			userRepository.save(user);
		});
		
	}
	
	/**
	 * Extracts the user's email from the given JWT token if present.
	 * <p>
	 * This method checks the token's claims for an "email" field. If the field exists,
	 * its value is converted to a {@link String} and returned wrapped in an {@link Optional}.
	 * If the field is not present, an empty Optional is returned.
	 * </p>
	 *
	 * @param token the JWT token containing user claims
	 * @return an {@code Optional<String>} containing the email if present; otherwise {@code Optional.empty()}
	 * @throws NullPointerException if the "email" claim is present but its value is {@code null}
	 */
	private Optional<String> getUserEmail(Jwt token) {
		Map<String, Object> attributes = token.getClaims();
		
		if (attributes.containsKey("email")) {
			String email = attributes.get("email").toString();
			log.debug("Email found in token claims: {}", email);
			return Optional.of(email);
		}
		
		log.warn("Email not found in token claims");
		return Optional.empty();
	}


}
