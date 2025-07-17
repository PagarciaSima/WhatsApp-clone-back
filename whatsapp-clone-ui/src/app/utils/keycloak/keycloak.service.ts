import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';

@Injectable({
  providedIn: 'root'
})
export class KeycloakService {

  private _keycloak: Keycloak | undefined;

  constructor() {

  }

  /**
   * Returns the Keycloak instance, initializing it if not already created.
   *
   * @returns The Keycloak client instance.
  */
  get keyCloak(): Keycloak {
    if (!this._keycloak) {
      this._keycloak = new Keycloak({
        url: 'http://localhost:9090',
        realm: 'whatsapp-clone',
        clientId: 'whatsapp-clone-app'
      });
    }
    return this._keycloak;
  }

  /**
   * Initializes Keycloak by calling its `init()` method.
   * Uses the "login-required" option to force login before loading the app.
   *
   * @returns A promise resolving to a boolean indicating whether the user is authenticated.
   */
  async init(): Promise<boolean | undefined> {
    const authenticated = await this.keyCloak.init({
      onLoad: 'login-required'
    });
    return authenticated;
  }

  /**
   * Triggers the Keycloak login process.
   *
   * This method redirects the user to the Keycloak login page
   * to authenticate if they are not already logged in.
   *
   * @returns A promise that resolves when the login process is initiated.
   */
  async login(): Promise<void> {
    await this.keyCloak.login();
  }

  /**
 * Gets the user ID (subject) from the parsed Keycloak token.
 *
 * @returns The user ID as a string.
 */
  get userId(): string {
    return this.keyCloak?.tokenParsed?.sub as string;
  }

  /**
   * Checks if the current Keycloak token is still valid (not expired).
   *
   * @returns `true` if the token is valid, otherwise `false`.
   */
  get isTokenValid(): boolean {
    return !this.keyCloak.isTokenExpired();
  }

  /**
 * Gets the full name of the user from the decoded Keycloak token.
 * 
 * @returns {string} The user's full name extracted from the 'name' claim in the token.
 */
  get fullName(): string {
    return this.keyCloak.tokenParsed?.['name'] as string;
  }

  /**
   * Initiates the logout process by redirecting the user to the specified URL.
   * 
   * Note: Currently calling `login()`, but it should be `logout()` instead.
   */
  logout() {
    this.keyCloak.login({ redirectUri: 'http://localhost:4200' });
    // Correct version:
    // this.keyCloak.logout({ redirectUri: 'http://localhost:4200' });
  }

  /**
   * Redirects the user to the Keycloak account management page.
   * 
   * @returns {Promise<void>} A promise that resolves when the redirection completes.
   */
  accountManagement() {
    return this.keyCloak.accountManagement();
  }
}