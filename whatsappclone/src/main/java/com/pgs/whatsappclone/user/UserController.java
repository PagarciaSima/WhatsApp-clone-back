package com.pgs.whatsappclone.user;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

	private final UserService userService;
	
	/**
	 * Retrieves a list of all users except the currently authenticated user.
	 *
	 * @param authentication the Spring Security authentication object representing the current user
	 * @return ResponseEntity containing a list of UserResponse objects with HTTP status 200 OK
	 */
	@GetMapping
	public ResponseEntity<List<UserResponse>> getAllUsers(Authentication authentication) {
	    log.info("Request received to fetch all users except the current authenticated user: {}", authentication.getName());
	    List<UserResponse> users = this.userService.getAllUsersExceptSelf(authentication);
	    log.info("Returning {} users", users.size());
	    return ResponseEntity.ok(users);
	}
}
