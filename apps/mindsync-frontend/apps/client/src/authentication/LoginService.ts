import type { WebClient } from '@/utils/WebClient';
import FetchWebClient from '@/utils/WebClient';
import type { AuthStore } from '@/stores';
import type { LoginRequest } from '@/authentication/domain/LoginRequest';
import type { AccessToken } from '@/authentication/domain/AccessToken';

export default class LoginService {
  private webClient: WebClient;

  constructor(private authStore: AuthStore) {
    this.webClient = new FetchWebClient('');
  }

  private url = `api/login`;

  async login(username: string, password: string, rememberMe: boolean) {
    const accessToken = await this.webClient.post<LoginRequest, AccessToken>(
      this.url,
      {
        username,
        password,
      }
    );
    await this.authStore.setAccessToken(accessToken, rememberMe);
  }

  async isAuthenticated(): Promise<boolean> {
    return this.authStore.isAuthenticated;
  }

  async logout() {
    this.authStore.token = undefined;
  }
}
