import Cookies from 'js-cookie';
import type { AuthStore } from '@/stores';
import type { LoginRequest } from '@/authentication/domain/LoginRequest';
import type { AccessToken } from '@/authentication/domain/AccessToken';
const CONTENT_TYPE: string = 'Content-Type';
export default class LoginService {
  constructor(private authStore: AuthStore) {}

  private url = `api/login`;

  async login(username: string, password: string, rememberMe: boolean) {
    const loginRequest: LoginRequest = { username, password };
    const headers = this.buildHeaders();
    const options: RequestInit = {
      method: 'POST',
      headers,
      body: JSON.stringify(loginRequest),
    };
    const response = await fetch(this.url, options);
    const accessToken: AccessToken = await response.json();
    await this.authStore.setAccessToken(accessToken, rememberMe);
  }

  async isAuthenticated(): Promise<boolean> {
    return this.authStore.isAuthenticated;
  }

  async logout() {
    await this.authStore.logout();
  }

  private buildHeaders(
    xrsfToken: string | undefined = Cookies.get('XSRF-TOKEN'),
    contentType: string = 'application/json'
  ): Headers {
    const headers = new Headers();
    if (xrsfToken) {
      headers.append('X-XSRF-TOKEN', xrsfToken);
    }
    headers.append(CONTENT_TYPE, contentType);
    return headers;
  }
}
