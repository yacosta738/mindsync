import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { createPinia, setActivePinia } from 'pinia';
import { useAuthStore } from '../../../../src/stores';
import LoginService from '../../../../src/authentication/application/LoginService';
import { AccessToken } from '../../../../src/authentication/domain/AccessToken';
import { LoginRequest } from '../../../../src/authentication/domain/LoginRequest';
import { createAFetchMockResponse } from '../../MockResponse';

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

const mockedFetch = vi.fn();
const headers = new Headers();
headers.append('Content-Type', 'application/json');

const loginRequest: LoginRequest = { username: 'test', password: 'test' };

const validateAccessTokenAttributes = (accessToken) => {
  expect(accessToken).toBeDefined();
  expect(accessToken?.token).toBe('test');
  expect(accessToken?.expiresIn).toBe(3600);
  expect(accessToken?.refreshToken).toBe('test');
  expect(accessToken?.refreshExpiresIn).toBe(3600);
  expect(accessToken?.tokenType).toBe('test');
  expect(accessToken?.notBeforePolicy).toBe(3600);
  expect(accessToken?.sessionState).toBe('test');
  expect(accessToken?.scope).toBe('test');
};

describe('login service', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
    global.fetch = mockedFetch;
    mockedFetch.mockReset();
  });
  afterEach(() => {
    mockedFetch.mockReset();
    delete globalThis.fetch;
  });

  it('should login and store in sessionStore', async () => {
    mockedFetch.mockResolvedValue(
      createAFetchMockResponse(200, mockAccessToken)
    );

    const authStore = useAuthStore();
    const loginService: LoginService = new LoginService(authStore);
    await loginService.login('test', 'test', false);
    expect(mockedFetch).toHaveBeenCalledWith('api/login', {
      method: 'POST',
      body: JSON.stringify(loginRequest),
      headers: headers,
    });
    const accessToken = authStore.accessToken;
    validateAccessTokenAttributes(accessToken);
  });

  it('should login and store in localStore', async () => {
    mockedFetch.mockResolvedValue(
      createAFetchMockResponse(200, mockAccessToken)
    );

    const authStore = useAuthStore();
    const loginService: LoginService = new LoginService(authStore);
    await loginService.login('test', 'test', true);
    expect(mockedFetch).toHaveBeenCalledWith('api/login', {
      method: 'POST',
      body: JSON.stringify(loginRequest),
      headers: headers,
    });
    const accessToken = authStore.accessToken;
    validateAccessTokenAttributes(accessToken);
  });

  it('should check if user is authenticated', async () => {
    mockedFetch.mockResolvedValue(
      createAFetchMockResponse(200, mockAccessToken)
    );
    const authStore = useAuthStore();
    const loginService: LoginService = new LoginService(authStore);
    await loginService.login('test', 'test', false);
    const isAuthenticated = await loginService.isAuthenticated();
    expect(isAuthenticated).toBeTruthy();
  });

  it('should logout', async () => {
    mockedFetch.mockResolvedValue(
      createAFetchMockResponse(200, mockAccessToken)
    );
    const authStore = useAuthStore();
    const loginService: LoginService = new LoginService(authStore);
    await loginService.login('test', 'test', false);
    await loginService.logout();
    const isAuthenticated = await loginService.isAuthenticated();
    expect(isAuthenticated).toBeFalsy();
  });
});
