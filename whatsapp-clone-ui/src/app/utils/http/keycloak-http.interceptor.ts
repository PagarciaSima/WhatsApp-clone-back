import { HttpHeaders, HttpInterceptorFn } from '@angular/common/http';
import { KeycloakService } from '../keycloak/keycloak.service';
import { inject } from '@angular/core';

// HTTP interceptor to add the Keycloak bearer token to outgoing requests
export const keycloakHttpInterceptor: HttpInterceptorFn = (req, next) => {
  // Inject the KeycloakService instance
  const keyCloakService: KeycloakService = inject(KeycloakService);
  
  // Get the token from the Keycloak service
  const token: string | undefined = keyCloakService.keyCloak.token;

  // If a token exists, clone the request and add the Authorization header
  if (token) {
    const authReq = req.clone({
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`
      })
    });
    // Pass the cloned request with the auth header to the next handler
    return next(authReq);
  }

  // If no token is available, pass the original request unmodified
  return next(req);
};
