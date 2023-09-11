import { beforeEach, afterEach, describe, expect, it, vi } from 'vitest';
import { createPinia, setActivePinia } from 'pinia';
import { useAuthStore } from '../../../../src/stores';
import RefreshTokenService from '../../../../src/authentication/application/RefreshTokenService';
import { AccessToken } from '../../../../src/authentication/domain/AccessToken';
import { createAFetchMockResponse } from '../../ResponseMocks';
import {
  createMockAccessToken,
  validateAccessTokenAttributes,
} from '../../AccessTokenMocks';
import { flushPromises } from '@vue/test-utils';

const mockAccessToken: AccessToken = createMockAccessToken();
const mockedRefreshAccessToken = {
  ...mockAccessToken,
  refreshToken: 'token test refresh',
};
const mockedFetch = vi.fn();
const headers = new Headers();
headers.append('Content-Type', 'application/json');

const refreshTokenRequest = { refreshToken: mockAccessToken.refreshToken };

describe('refresh token service', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
    global.fetch = mockedFetch;
    mockedFetch.mockReset();
  });
  afterEach(() => {
    mockedFetch.mockReset();
  });

  it('should refresh token and store in sessionStore', async () => {
    mockedFetch.mockResolvedValue(
      createAFetchMockResponse(200, mockedRefreshAccessToken)
    );

    const authStore = useAuthStore();
    await authStore.setAccessToken(mockAccessToken);
    const refreshTokenService: RefreshTokenService = new RefreshTokenService(
      authStore
    );
    await refreshTokenService.refreshToken();
    expect(mockedFetch).toHaveBeenCalledWith('api/refresh-token', {
      method: 'POST',
      body: JSON.stringify(refreshTokenRequest),
      headers: headers,
    });
    const accessToken = authStore.accessToken as AccessToken;
    validateAccessTokenAttributes(accessToken, mockedRefreshAccessToken);
  });

  it('should refresh token and store in localStore', async () => {
    mockedFetch.mockResolvedValue(
      createAFetchMockResponse(200, mockedRefreshAccessToken)
    );

    const authStore = useAuthStore();
    await authStore.setSessionActive(true);
    await authStore.setAccessToken(mockAccessToken);
    const refreshTokenService: RefreshTokenService = new RefreshTokenService(
      authStore
    );
    await refreshTokenService.refreshToken();

    expect(mockedFetch).toHaveBeenCalledWith('api/refresh-token', {
      method: 'POST',
      body: JSON.stringify(refreshTokenRequest),
      headers: headers,
    });
    const accessToken = authStore.accessToken as AccessToken;
    validateAccessTokenAttributes(accessToken, mockedRefreshAccessToken);
  });

  it('should not refresh token if refreshToken is not defined', async () => {
    const authStore = useAuthStore();
    await authStore.setAccessToken({
      ...mockAccessToken,
      token: '',
      refreshToken: '',
    });
    const refreshTokenService: RefreshTokenService = new RefreshTokenService(
      authStore
    );
    await refreshTokenService.refreshToken();
    await flushPromises();
    expect(mockedFetch).not.toHaveBeenCalled();
    const accessToken = authStore.accessToken;
    expect(accessToken).toBeNull();
  });

  it('should logout if refresh token fails', async () => {
    mockedFetch.mockResolvedValue(createAFetchMockResponse(401, null));

    const authStore = useAuthStore();
    await authStore.setAccessToken(mockAccessToken);
    const refreshTokenService: RefreshTokenService = new RefreshTokenService(
      authStore
    );
    await refreshTokenService.refreshToken();
    expect(mockedFetch).toHaveBeenCalledWith('api/refresh-token', {
      method: 'POST',
      body: JSON.stringify(refreshTokenRequest),
      headers: headers,
    });
    const accessToken = authStore.accessToken;
    expect(accessToken).toBeNull();
  });

  it('should logout if refresh token fails (401) and session is active', async () => {
    mockedFetch.mockResolvedValue(createAFetchMockResponse(401, null));

    const authStore = useAuthStore();
    await authStore.setSessionActive(true);
    await authStore.setAccessToken(mockAccessToken);
    const refreshTokenService: RefreshTokenService = new RefreshTokenService(
      authStore
    );
    await refreshTokenService.refreshToken();
    expect(mockedFetch).toHaveBeenCalledWith('api/refresh-token', {
      method: 'POST',
      body: JSON.stringify(refreshTokenRequest),
      headers: headers,
    });
    const accessToken = authStore.accessToken;
    expect(accessToken).toBeNull();
  });

  it('should logout if refresh token fails (400)', async () => {
    mockedFetch.mockResolvedValue(
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
    await authStore.setAccessToken(mockAccessToken);
    const refreshTokenService: RefreshTokenService = new RefreshTokenService(
      authStore
    );
    await refreshTokenService.refreshToken();
    expect(mockedFetch).toHaveBeenCalledWith('api/refresh-token', {
      method: 'POST',
      body: JSON.stringify(refreshTokenRequest),
      headers: headers,
    });
    const accessToken = authStore.accessToken;
    expect(accessToken).toBeNull();
  });

  it('should logout if refresh token fails (403)', async () => {
    mockedFetch.mockResolvedValue(
      createAFetchMockResponse(403, {
        type: 'https://mindsync.io/errors/forbidden',
        title: 'Forbidden',
        status: 403,
        detail: 'Could not refresh access token',
        instance: '/api/refresh-token',
        errorCategory: 'FORBIDDEN',
        timestamp: 1693774321,
      })
    );

    const authStore = useAuthStore();
    await authStore.setAccessToken(mockAccessToken);
    const refreshTokenService: RefreshTokenService = new RefreshTokenService(
      authStore
    );
    await refreshTokenService.refreshToken();
    expect(mockedFetch).toHaveBeenCalledWith('api/refresh-token', {
      method: 'POST',
      body: JSON.stringify(refreshTokenRequest),
      headers: headers,
    });
    const accessToken = authStore.accessToken;
    expect(accessToken).toBeNull();
  });
});
