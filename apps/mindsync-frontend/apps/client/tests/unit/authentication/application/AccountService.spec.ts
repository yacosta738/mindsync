import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { createPinia, setActivePinia } from 'pinia';
import { useAuthStore } from '../../../../src/stores';
import AccountService from '../../../../src/authentication/application/AccountService';
import { AccessToken } from '../../../../src/authentication/domain/AccessToken';
import { createAFetchMockResponse } from '../../MockResponse';
import RefreshTokenService from '../../../../src/authentication/application/RefreshTokenService';
import User from '../../../../src/authentication/domain/User';
import { Authority } from '../../../../src/authentication/domain/Authority';

const mockAccessToken: AccessToken = {
  token: 'test',
  expiresIn: 3600,
  refreshToken: 'test',
  refreshExpiresIn: 3600,
  tokenType: 'test',
  notBeforePolicy: 3600,
  sessionState: 'test',
  scope: 'test',
};

const mockUser: User = {
  id: 'test',
  username: 'test',
  email: 'test@gmail.com',
  firstName: 'test',
  lastName: 'test',
  authorities: [Authority.USER],
};

const mockedFetch = vi.fn();
const headers = new Headers();
headers.append('Content-Type', 'application/json');

const validateUserAttributes = (user: User) => {
  expect(user).toBeDefined();
  expect(user.id).toBe('test');
  expect(user.username).toBe('test');
  expect(user.email).toBe('test@gmail.com');
  expect(user.firstName).toBe('test');
  expect(user.lastName).toBe('test');
  expect(user.authorities).toBeDefined();
  expect(user.authorities.length).toBe(1);
  expect(user.authorities[0]).toBe(Authority.USER);
};

describe('account service', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
    global.fetch = mockedFetch;
    mockedFetch.mockReset();
  });
  afterEach(() => {
    mockedFetch.mockReset();
    delete globalThis.fetch;
  });

  it('should retrieve account from server', async () => {
    mockedFetch.mockResolvedValueOnce(createAFetchMockResponse(200, mockUser));

    const authStore = useAuthStore();
    const refreshTokenService: RefreshTokenService = new RefreshTokenService(
      authStore
    );
    const loginService: AccountService = new AccountService(
      authStore,
      refreshTokenService
    );
    const user = await loginService.retrieveAccountFromServer();
    expect(mockedFetch).toHaveBeenCalledWith('api/account', {
      headers: headers,
    });
    const userIdentity = authStore.userIdentity;
    validateUserAttributes(user);
    validateUserAttributes(userIdentity);
  });

  it('should retrieve account from server with refresh token', async () => {
    mockedFetch
      .mockResolvedValueOnce(createAFetchMockResponse(401, null))
      .mockResolvedValueOnce(createAFetchMockResponse(200, mockAccessToken))
      .mockResolvedValueOnce(createAFetchMockResponse(200, mockUser));

    const authStore = useAuthStore();
    const refreshTokenService: RefreshTokenService = new RefreshTokenService(
      authStore
    );
    const loginService: AccountService = new AccountService(
      authStore,
      refreshTokenService
    );
    await authStore.setAccessToken(mockAccessToken);
    const user = await loginService.retrieveAccountFromServer();
    expect(mockedFetch).toHaveBeenCalledWith('api/refresh-token', {
      method: 'POST',
      body: JSON.stringify({ refreshToken: mockAccessToken.refreshToken }),
      headers: headers,
    });
    const userIdentity = authStore.userIdentity;
    validateUserAttributes(user);
    validateUserAttributes(userIdentity);
  });

  it('should retrieve account from server with refresh token and logout', async () => {
    mockedFetch
      .mockResolvedValueOnce(createAFetchMockResponse(401, null))
      .mockResolvedValueOnce(
        createAFetchMockResponse(400, {
          type: 'https://mindsync.io/errors/bad-request',
          title: 'Bad request',
          status: 400,
          detail: 'Could not refresh access token',
          instance: '/api/refresh-token',
          errorCategory: 'BAD_REQUEST',
          timestamp: 1693774321,
        })
      );

    const authStore = useAuthStore();
    const refreshTokenService: RefreshTokenService = new RefreshTokenService(
      authStore
    );
    const loginService: AccountService = new AccountService(
      authStore,
      refreshTokenService
    );
    await authStore.setAccessToken(mockAccessToken);
    await loginService.retrieveAccountFromServer();
    expect(mockedFetch).toHaveBeenCalledWith('api/refresh-token', {
      method: 'POST',
      body: JSON.stringify({ refreshToken: mockAccessToken.refreshToken }),
      headers: headers,
    });
    const userIdentity = authStore.userIdentity;
    expect(userIdentity).toBeUndefined();
  });
});
