package com.pgs.whatsappclone.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

public class KeyCloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

	@Override
	/**
	 * Converts a {@link Jwt} token into an {@link AbstractAuthenticationToken} by extracting
	 * granted authorities from both the standard JWT claims and custom resource roles.
	 *
	 * <p>This method combines the authorities provided by the default {@link JwtGrantedAuthoritiesConverter}
	 * with additional roles extracted from custom claims (e.g., resource-specific roles).
	 *
	 * @param source the JWT token to convert; must not be null
	 * @return an {@link AbstractAuthenticationToken} containing the JWT and the combined authorities
	 */
	public AbstractAuthenticationToken convert(@NonNull Jwt source) {
		return new JwtAuthenticationToken(
			source,
			Stream.concat(
				new JwtGrantedAuthoritiesConverter().convert(source).stream(),
				extractResourceRoles(source).stream()
			).collect(Collectors.toSet())
		);
	}


	/**
	 * Extracts the roles assigned to the user from the "resource_access" claim in the JWT.
	 * 
	 * The "resource_access" claim contains a map of clients registered in Keycloak,
	 * each with its own set of roles assigned to the user.
	 * This method specifically extracts roles from the "account" client.
	 * 
	 * It converts each role into a Spring Security {@link SimpleGrantedAuthority} with the prefix "ROLE_"
	 * and replaces hyphens with underscores to conform with Spring Security naming conventions.
	 * 
	 * @param jwt the JWT token containing the claims
	 * @return a collection of granted authorities representing the user's roles
	 */
	private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
	    // Extract the resource_access map from the JWT claims, which contains client-role mappings
	    var resourceAccess = new HashMap<>(jwt.getClaim("resource_access"));

	    // Get the roles map for the "account" client; keys are strings, values are lists of role names
	    var eternal = (Map<String, List<String>>) resourceAccess.get("account");

	    // Get the list of roles for the "account" client
	    var roles = eternal.get("roles");

	    // Convert each role into a GrantedAuthority with "ROLE_" prefix and replace hyphens with underscores (spring sec convention)
	    return roles.stream()
	            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.replace("-", "_")))
	            .collect(Collectors.toSet());
	}
	
	@Bean
	/**
	 * Configures a global CORS filter to allow cross-origin requests from the frontend.
	 * 
	 * This filter enables the backend to accept requests from http://localhost:4200,
	 * including credentials (e.g. cookies or tokens) and standard HTTP headers.
	 * 
	 * It allows common HTTP methods like GET, POST, PUT, DELETE, etc., and applies the
	 * CORS policy to all endpoints (/**).
	 * 
	 * @return a configured CorsFilter bean
	 */
	public CorsFilter corsFilter() {
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		final CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
		config.setAllowedHeaders(Arrays.asList(
				HttpHeaders.ORIGIN,
				HttpHeaders.CONTENT_TYPE,
				HttpHeaders.ACCEPT,
				HttpHeaders.AUTHORIZATION
		));
		
		config.setAllowedMethods(Arrays.asList(
				"GET",
				"POST",
				"DELETE",
				"PUT",
				"OPTIONS",
				"PATCH"
		));
		
		// For all url and resources
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

}
