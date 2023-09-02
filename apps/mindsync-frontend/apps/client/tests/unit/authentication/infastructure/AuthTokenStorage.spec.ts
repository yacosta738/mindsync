import {
  OAuthTokenManager,
  SecureTokenRepository,
} from '../../../../src/authentication/infrastructure/AuthTokenStorage';
import { describe, expect, it } from 'vitest';

describe('should operate on local storage', () => {
  const localStorageTokenManager: SecureTokenRepository = new OAuthTokenManager(
    localStorage
  );

  it('should store values in local storage', () => {
    localStorageTokenManager.set('test');
    expect(localStorageTokenManager.get()).toBe('test');
  });

  it('should clear values in local storage', () => {
    localStorageTokenManager.clear();
    expect(localStorageTokenManager.get()).toBe(null);
  });
});

describe('should operate on session storage', () => {
  const sessionStorageTokenManager: SecureTokenRepository =
    new OAuthTokenManager(sessionStorage);

  it('should store values in session storage', () => {
    sessionStorageTokenManager.set('test');
    expect(sessionStorageTokenManager.get()).toBe('test');
  });

  it('should clear values in session storage', () => {
    sessionStorageTokenManager.clear();
    expect(sessionStorageTokenManager.get()).toBe(null);
  });

  it('should store values in session storage', () => {
    sessionStorageTokenManager.set('test');
    expect(sessionStorageTokenManager.get()).toBe('test');
  });
});
