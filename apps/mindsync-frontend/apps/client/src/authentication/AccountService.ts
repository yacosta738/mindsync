import type User from '@/authentication/domain/User';
import type { AuthStore } from '@/stores';
import Cookies from 'js-cookie';
import type RefreshTokenService from '@/authentication/RefreshTokenService';

const CONTENT_TYPE: string = 'Content-Type';
export default class AccountService {
  constructor(
    private authStore: AuthStore,
    private refreshTokenService: RefreshTokenService
  ) {}

  private url = `api/account`;

  async retrieveAccountFromServer(): Promise<User> {
    const headers = this.buildHeaders();
    const response = await fetch(this.url, { headers });
    if (response.status === 401) {
      await this.refreshTokenService.refreshToken();
      return this.retrieveAccountFromServer();
    }
    const user = await response.json();
    await this.authStore.setIdentity(user);
    return user;
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
    const accessToken = this.authStore.accessToken?.token;
    console.log('accessToken in buildHeaders', accessToken);
    if (accessToken) {
      headers.append('Authorization', `Bearer ${accessToken}`);
    }
    return headers;
  }
}
