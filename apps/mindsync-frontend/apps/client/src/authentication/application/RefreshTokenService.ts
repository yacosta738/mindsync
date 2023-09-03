import type { AuthStore } from '@/stores';
import Cookies from 'js-cookie';

const CONTENT_TYPE: string = 'Content-Type';
export default class RefreshTokenService {
  private url = `api/refresh-token`;

  constructor(private authStore: AuthStore) {}

  async refreshToken() {
    const refreshToken = this.authStore.accessToken?.refreshToken;

    if (refreshToken) {
      const headers = this.buildHeaders();
      const options: RequestInit = {
        method: 'POST',
        headers,
        body: JSON.stringify({ refreshToken }),
      };
      const response = await fetch(this.url, options);
      if (
        response.status === 400 ||
        response.status === 401 ||
        response.status === 403
      ) {
        await this.authStore.logout();
        return;
      }
      const accessToken = await response.json();
      await this.authStore.setAccessToken(accessToken);
    }
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
