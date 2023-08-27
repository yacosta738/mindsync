import type { AccessToken } from '@/authentication/domain/AccessToken';
import { useAuthStore } from '@/stores/auth.store';
import Cookies from 'js-cookie';

export interface WebClient {
  get<T>(path: string): Promise<T>;
  post<T, U>(path: string, data: T): Promise<U>;
  put<T, U>(path: string, data: T): Promise<U>;
  delete(path: string): Promise<void>;
  patch<T, U>(path: string, data: T): Promise<U>;
}

const CONTENT_TYPE = 'Content-Type';
export default class FetchWebClient implements WebClient {
  private readonly baseUrl: string;

  private readonly authStore = useAuthStore();

  private readonly contentTypes = {
    json: 'application/json',
    v1: 'application/vnd.api.v1+json',
  };

  constructor(baseUrl: string) {
    this.baseUrl = baseUrl;
  }

  private includesAnyContentType(
    contentType: string,
    contentTypes: { json: string; v1: string }
  ) {
    return (
      contentType.includes(contentTypes.json) ||
      contentType.includes(contentTypes.v1)
    );
  }

  private async interceptRequest(options: RequestInit): Promise<RequestInit> {
    const authToken = await this.getAuthToken();
    options.headers = {
      ...options.headers,
      Authorization: `Bearer ${authToken.token}`,
    };
    return options;
  }

  private async interceptResponse(response: Response): Promise<Response> {
    if (response.status === 401) {
      await this.refreshAuthToken();
      const originalRequest = response.clone();
      return fetch(originalRequest.url, originalRequest);
    }
    return response;
  }

  private async getAuthToken(): Promise<AccessToken> {
    const token = this.authStore.accessToken;
    if (!token) {
      throw new Error('No access token found');
    }
    return token;
  }

  private async refreshAuthToken(): Promise<void> {
    // Lógica para refrescar el token de autenticación

    const token = await this.getAuthToken();
    const refreshToken = token.refreshToken;
    if (!refreshToken) {
      throw new Error('No refresh token found');
    }
    const url = `${this.baseUrl}/api/auth/refresh`;
    const headers = this.buildHeaders();
    const options: RequestInit = {
      method: 'POST',
      headers,
      body: JSON.stringify({ refreshToken }),
    };
    const response = await fetch(url, options);
    const contentType = response.headers.get(CONTENT_TYPE);
    if (
      contentType &&
      this.includesAnyContentType(contentType, this.contentTypes)
    ) {
      const newToken = await response.json();
      await this.authStore.setAccessToken(newToken);
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

  private async request<T, U>(
    method: string,
    path: string,
    data?: U
  ): Promise<T> {
    const url = `${this.baseUrl}${path}`;
    const headers = this.buildHeaders();

    let options: RequestInit = {
      method,
      headers,
    };

    if (data) {
      options.body = JSON.stringify(data);
    }

    options = await this.interceptRequest(options);

    const response = await fetch(url, options);

    const interceptedResponse = await this.interceptResponse(response);

    if (!interceptedResponse.ok) {
      const errorMessage = await interceptedResponse.text();
      throw new Error(
        `Request failed with status ${interceptedResponse.status}: ${errorMessage}`
      );
    }

    const contentType = interceptedResponse.headers.get(CONTENT_TYPE);
    if (
      contentType &&
      this.includesAnyContentType(contentType, this.contentTypes)
    ) {
      return interceptedResponse.json();
    } else {
      throw new Error('Response is not in JSON format');
    }
  }

  async get<T>(path: string): Promise<T> {
    const controller = new AbortController();
    const signal = controller.signal;

    const requestPromise = this.request<T, void>('GET', path);

    signal.addEventListener('abort', () => {
      controller.abort();
    });

    return requestPromise;
  }

  async post<T, U>(path: string, data: T): Promise<U> {
    return this.request<U, T>('POST', path, data);
  }

  async put<T, U>(path: string, data: T): Promise<U> {
    return this.request<U, T>('PUT', path, data);
  }

  async patch<T, U>(path: string, data: T): Promise<U> {
    return this.request<U, T>('PATCH', path, data);
  }

  async delete(path: string): Promise<void> {
    await this.request<void, void>('DELETE', path);
  }
}
