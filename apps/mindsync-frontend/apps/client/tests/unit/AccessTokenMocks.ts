import { expect } from 'vitest';
import { AccessToken } from '../../src/authentication/domain/AccessToken';

export const validateAccessTokenAttributes = (accessToken: AccessToken) => {
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

export const createMockAccessToken = (
  token: string = 'test',
  expiresIn: number = 3600,
  refreshToken: string = 'test',
  refreshExpiresIn: number = 3600,
  tokenType: string = 'test',
  notBeforePolicy: number = 3600,
  sessionState: string = 'test',
  scope: string = 'test'
): AccessToken => {
  return {
    token,
    expiresIn,
    refreshToken,
    refreshExpiresIn,
    tokenType,
    notBeforePolicy,
    sessionState,
    scope,
  };
};
