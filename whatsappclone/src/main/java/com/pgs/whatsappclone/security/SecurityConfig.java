package com.pgs.whatsappclone.security;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
	/**
	 * Configures the application's HTTP security filter chain.
	 *
	 * <p>This method sets up CORS with default settings, disables CSRF protection,
	 * and defines authorization rules for incoming HTTP requests. It allows unauthenticated
	 * access to certain endpoints (such as Swagger and WebSocket routes), while securing all
	 * other endpoints by requiring authentication using JWT tokens issued by an OAuth2
	 * resource server (Keycloak in this case).</p>
	 *
	 * <p>A custom {@code JwtAuthenticationConverter} is provided to extract roles from the
	 * Keycloak-issued token (typically from {@code realm_access.roles}).</p>
	 *
	 * @param http the {@link HttpSecurity} to configure
	 * @return the configured {@link SecurityFilterChain}
	 * @throws Exception if an error occurs during configuration
	 */
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.cors(Customizer.withDefaults())
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(req -> 
				req.requestMatchers(
						// swagger
						"/v3/api-docs",
						"/v3/api-docs/**",
						"/swagger/resources",
						"/swagger/resources/**",
						"/configuration/ui",
						"/configuration/security",
						"/swagger-ui/**",
						"/webjars/**",
						"/swagger-ui.html",
						// websocket
						"/ws/**")
					.permitAll()
				.anyRequest()
				.authenticated()
					)
			// Token auth with custom converter
			.oauth2ResourceServer(auth -> 
					auth.jwt(token -> 
							token.jwtAuthenticationConverter(new KeyCloakJwtAuthenticationConverter())));
		return http.build();
	}
	
	  @Bean
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
	                "PATCH"
	        ));
	        source.registerCorsConfiguration("/**", config);
	        return new CorsFilter(source);

	    }
}
