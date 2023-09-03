import { beforeEach, describe, expect, it } from 'vitest';
import { createPinia, setActivePinia } from 'pinia';
import { AccessToken } from '../../../src/authentication/domain/AccessToken';
import { useAuthStore } from '../../../src/stores';
import User from '../../../src/authentication/domain/User';
import { Authority } from '../../../src/authentication/domain/Authority';

const accessToken: AccessToken = {
  token: 'test',
  expiresIn: 3600,
  refreshToken: 'test',
  refreshExpiresIn: 3600,
  tokenType: 'test',
  notBeforePolicy: 3600,
  sessionState: 'test',
  scope: 'test',
};

describe('auth store', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
  });

  it('should set access token', async () => {
    const authStore = useAuthStore();
    await authStore.setAccessToken(accessToken);
    const storedToken = authStore.accessToken;
    expect(storedToken.token).toBe(accessToken.token);
    expect(storedToken.expiresIn).toBe(accessToken.expiresIn);
    expect(storedToken.refreshToken).toBe(accessToken.refreshToken);
    expect(storedToken.refreshExpiresIn).toBe(accessToken.refreshExpiresIn);
    expect(storedToken.tokenType).toBe(accessToken.tokenType);
    expect(storedToken.notBeforePolicy).toBe(accessToken.notBeforePolicy);
    expect(storedToken.sessionState).toBe(accessToken.sessionState);
    expect(storedToken.scope).toBe(accessToken.scope);
  });

  it('should set identity', async () => {
    const authStore = useAuthStore();
    const user: User = {
      id: 'test',
      username: 'test',
      email: 'test',
      firstName: 'test',
      lastName: 'test',
      authorities: [Authority.USER],
    };
    await authStore.setIdentity(user);
    const storedUser = authStore.userIdentity;
    expect(storedUser.id).toBe(user.id);
    expect(storedUser.username).toBe(user.username);
    expect(storedUser.email).toBe(user.email);
    expect(storedUser.firstName).toBe(user.firstName);
    expect(storedUser.lastName).toBe(user.lastName);
    storedUser.authorities.forEach((authority, index) => {
      expect(authority).toBe(user.authorities[index]);
    });
  });

  it('should check if user has any authority', async () => {
    const authStore = useAuthStore();
    const user: User = {
      id: 'test',
      username: 'test',
      email: 'test',
      firstName: 'test',
      lastName: 'test',
      authorities: [Authority.USER],
    };
    await authStore.setIdentity(user);
    const hasAuthority = await authStore.hasAnyAuthority([Authority.USER]);
    expect(hasAuthority).toBe(true);
  });

  it('should check if user is authenticated', async () => {
    const authStore = useAuthStore();
    const user: User = {
      id: 'test',
      username: 'test',
      email: 'test',
      firstName: 'test',
      lastName: 'test',
      authorities: [Authority.USER],
    };
    await authStore.setIdentity(user);
    await authStore.setAccessToken(accessToken);
    await authStore.authenticate('/test-route');
    expect(authStore.url).toBe('/test-route');
    expect(authStore.isAuthenticated).toBe(true);
  });

  it('should set session active', async () => {
    const authStore = useAuthStore();
    await authStore.setAccessToken(accessToken, true);
    expect(authStore.sessionActive).toBe(true);
  });

  it('should logout user', async () => {
    const authStore = useAuthStore();
    await authStore.logout();
    expect(authStore.isAuthenticated).toBe(false);
    expect(authStore.accessToken).toBe(null);
    expect(authStore.userIdentity).toBe(null);
    expect(authStore.url).toBe('/');
  });
});
