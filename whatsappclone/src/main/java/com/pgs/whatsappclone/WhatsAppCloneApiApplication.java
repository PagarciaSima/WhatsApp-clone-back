package com.pgs.whatsappclone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SpringBootApplication
@EnableJpaAuditing
@SecurityScheme(
	    name = "keycloak",                       // Name of the security scheme (reference in Swagger)
	    type = SecuritySchemeType.OAUTH2,       // Security type: OAuth2
	    bearerFormat = "JWT",                    // Token format used (JSON Web Token)
	    scheme = "bearer",                       // HTTP authorization scheme type (bearer token)
	    in = SecuritySchemeIn.HEADER,            // Token will be sent in the HTTP Authorization header
	    flows = @OAuthFlows(
	        password = @OAuthFlow(               // OAuth2 flow of type "password" (Resource Owner Password Credentials)
	            authorizationUrl = "http://localhost:9090/realms/whatsapp-clone/protocol/openid-connect/auth", // Authorization URL (not often used in this flow)
	            tokenUrl = "http://localhost:9090/realms/whatsapp-clone/protocol/openid-connect/token" // URL where the token is requested by sending username and password
	        )
	    )
)
// localhost:8080/swagger-ui(index.html#/
public class WhatsAppCloneApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WhatsAppCloneApiApplication.class, args);
	}

}
