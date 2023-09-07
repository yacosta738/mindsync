import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { createPinia, setActivePinia } from 'pinia';
import { useAuthStore } from '../../../../src/stores';
import AccountService from '../../../../src/authentication/application/AccountService';
import { AccessToken } from '../../../../src/authentication/domain/AccessToken';
import { createAFetchMockResponse } from '../../ResponseMocks';
import RefreshTokenService from '../../../../src/authentication/application/RefreshTokenService';
import User from '../../../../src/authentication/domain/User';
import { createMockAccessToken } from '../../AccessTokenMocks';
import { createMockUser, compareUserAttributes } from '../../UserMocks';

const mockAccessToken: AccessToken = createMockAccessToken();

const mockUser: User = createMockUser();

const mockedFetch = vi.fn();
const headers = new Headers();
headers.append('Content-Type', 'application/json');

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
    const accountService: AccountService = new AccountService(
      authStore,
      refreshTokenService
    );
    const user = await accountService.retrieveAccountFromServer();
    expect(mockedFetch).toHaveBeenCalledWith('api/account', {
      headers: headers,
    });
    const userIdentity = authStore.userIdentity;
    compareUserAttributes(user);
    compareUserAttributes(userIdentity);
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
    const accountService: AccountService = new AccountService(
      authStore,
      refreshTokenService
    );
    await authStore.setAccessToken(mockAccessToken);
    const user = await accountService.retrieveAccountFromServer();
    expect(mockedFetch).toHaveBeenCalledWith('api/refresh-token', {
      method: 'POST',
      body: JSON.stringify({ refreshToken: mockAccessToken.refreshToken }),
      headers: headers,
    });
    const userIdentity = authStore.userIdentity;
    compareUserAttributes(user);
    compareUserAttributes(userIdentity);
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
    const accountService: AccountService = new AccountService(
      authStore,
      refreshTokenService
    );
    await authStore.setAccessToken(mockAccessToken);
    await accountService.retrieveAccountFromServer();
    expect(mockedFetch).toHaveBeenCalledWith('api/refresh-token', {
      method: 'POST',
      body: JSON.stringify({ refreshToken: mockAccessToken.refreshToken }),
      headers: headers,
    });
    const userIdentity = authStore.userIdentity;
    expect(userIdentity).toBeUndefined();
  });
});
