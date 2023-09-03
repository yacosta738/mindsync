import { beforeEach, afterEach, describe, expect, it, vi } from 'vitest';
import { createPinia, setActivePinia } from 'pinia';
import { useAuthStore } from '../../../../src/stores';
import RefreshTokenService from '../../../../src/authentication/application/RefreshTokenService';
import { AccessToken } from '../../../../src/authentication/domain/AccessToken';

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
const mockedRefreshAccessToken = {
  ...mockAccessToken,
  refreshToken: 'token test refresh',
};
const mockedFetch = vi.fn();
const headers = new Headers();
headers.append('Content-Type', 'application/json');
const createAFetchMockResponse = (status: number, json: AccessToken) => {
  return {
    status: status, // Puedes ajustar esto según tus necesidades
    json: async () => json, // El objeto que devuelve el backend
  };
};

const refreshTokenRequest = { refreshToken: mockAccessToken.refreshToken };

describe('refresh token service', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
    global.fetch = mockedFetch;
    mockedFetch.mockReset();
  });
  afterEach(() => {
    mockedFetch.mockReset();
    delete globalThis.fetch;
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
    const accessToken = authStore.accessToken;
    expect(accessToken).toBeDefined();
    expect(accessToken?.token).toBe('test');
    expect(accessToken?.expiresIn).toBe(3600);
    expect(accessToken?.refreshToken).toBe('token test refresh');
    expect(accessToken?.refreshExpiresIn).toBe(3600);
    expect(accessToken?.tokenType).toBe('test');
    expect(accessToken?.notBeforePolicy).toBe(3600);
    expect(accessToken?.sessionState).toBe('test');
    expect(accessToken?.scope).toBe('test');
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
    const accessToken = authStore.accessToken;
    expect(accessToken).toBeDefined();
    expect(accessToken?.token).toBe('test');
    expect(accessToken?.expiresIn).toBe(3600);
    expect(accessToken?.refreshToken).toBe('token test refresh');
    expect(accessToken?.refreshExpiresIn).toBe(3600);
    expect(accessToken?.tokenType).toBe('test');
    expect(accessToken?.notBeforePolicy).toBe(3600);
    expect(accessToken?.sessionState).toBe('test');
    expect(accessToken?.scope).toBe('test');
  });

  it('should not refresh token if refreshToken is not defined', async () => {
    const authStore = useAuthStore();
    await authStore.setAccessToken(null);
    const refreshTokenService: RefreshTokenService = new RefreshTokenService(
      authStore
    );
    await refreshTokenService.refreshToken();
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

  it('should logout if refresh token fails and session is active', async () => {
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
});
