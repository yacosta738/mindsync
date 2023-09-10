export interface SecureTokenRepository {
  get(): string | null;
  set(token: string): void;
  clear(): void;
}
export const TOKEN_KEY: string = 'accessToken';
export class OAuthTokenManager implements SecureTokenRepository {
  private storage: Storage;

  constructor(storage: Storage = localStorage) {
    this.storage = storage;
  }

  get(): string | null {
    return this.storage.getItem(TOKEN_KEY);
  }

  set(token: string): void {
    this.storage.setItem(TOKEN_KEY, token);
  }

  clear(): void {
    this.storage.removeItem(TOKEN_KEY);
  }
}
