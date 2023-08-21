import type { WebClient } from '@/utils/webclient';
import { FetchWebClient } from '@/utils/webclient';
import type { AuthStore } from '@/stores';
import type { LoginRequest } from '@/authentication/domain/LoginRequest';
import type { AccessToken } from '@/authentication/domain/AccessToken';

const TOKEN_KEY: string = 'accessToken';

export default class LoginService {
  private webClient: WebClient<AccessToken, LoginRequest>;

  constructor(private authStore: AuthStore) {
    this.webClient = new FetchWebClient<AccessToken, LoginRequest>();
  }

  private url = `api/login`;

  async login(username: string, password: string, rememberMe: boolean) {
    const accessToken = await this.webClient.post(this.url, {
      username,
      password,
    });
    await this.storeToken(accessToken, rememberMe);
    await this.authStore.setAccessToken(accessToken);
  }

  private async storeToken(accessToken: AccessToken, rememberMe = true) {
    if (rememberMe) {
      localStorage.setItem(TOKEN_KEY, JSON.stringify(accessToken));
    } else {
      sessionStorage.setItem(TOKEN_KEY, JSON.stringify(accessToken));
    }
  }
}
