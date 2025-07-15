package com.pgs.whatsappclone.ws;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.messaging.context.AuthenticationPrincipalArgumentResolver;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
	    // Enable a simple in-memory message broker and set the destination prefix for messages it will handle
	    registry.enableSimpleBroker("/user");
	    
	    // Set prefix for messages bound for methods annotated with @MessageMapping
	    registry.setApplicationDestinationPrefixes("/app");
	    
	    // Set prefix used to identify user-specific destinations (e.g., for private messages)
	    registry.setUserDestinationPrefix("/user");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
	    // Register the /ws endpoint, clients will use this to connect to the WebSocket server
	    registry
	        .addEndpoint("/ws")
	        // Allow cross-origin requests only from Angular dev server at localhost:4200
	        .setAllowedOrigins("http://localhost:4200")
	        // Enable SockJS fallback options for browsers that don't support native WebSockets
	        .withSockJS();
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
	    // Adds a custom argument resolver to support injecting the authenticated user
	    // directly into controller method parameters annotated with @AuthenticationPrincipal.
	    argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
	}
	
	@Override
	public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
	    // Create a content type resolver to set the default MIME type to JSON
	    DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
	    resolver.setDefaultMimeType(MediaType.APPLICATION_JSON);

	    // Create a Jackson message converter for converting messages to/from JSON
	    MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
	    
	    // Set a new ObjectMapper for JSON serialization/deserialization
	    converter.setObjectMapper(new ObjectMapper());
	    
	    // Assign the content type resolver to the converter
	    converter.setContentTypeResolver(resolver);

	    // Add the converter to the list of message converters
	    messageConverters.add(converter);
	    
	    // Return false to indicate that default converters should still be added by Spring
	    return false;
	}
	
	
}
