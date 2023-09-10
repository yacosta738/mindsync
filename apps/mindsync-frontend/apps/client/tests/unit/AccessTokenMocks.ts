import { expect } from 'vitest';
import { AccessToken } from '../../src/authentication/domain/AccessToken';

export const validateAccessTokenAttributes = (
  accessToken: AccessToken,
  expectedAccessTokenValues: AccessToken = {
    token: 'test',
    expiresIn: 3600,
    refreshToken: 'test',
    refreshExpiresIn: 3600,
    tokenType: 'test',
    notBeforePolicy: 3600,
    sessionState: 'test',
    scope: 'test',
  }
) => {
  expect(accessToken).toBeDefined();
  expect(accessToken?.token).toBe(expectedAccessTokenValues?.token);
  expect(accessToken?.expiresIn).toBe(expectedAccessTokenValues?.expiresIn);
  expect(accessToken?.refreshToken).toBe(
    expectedAccessTokenValues?.refreshToken
  );
  expect(accessToken?.refreshExpiresIn).toBe(
    expectedAccessTokenValues?.refreshExpiresIn
  );
  expect(accessToken?.tokenType).toBe(expectedAccessTokenValues?.tokenType);
  expect(accessToken?.notBeforePolicy).toBe(
    expectedAccessTokenValues?.notBeforePolicy
  );
  expect(accessToken?.sessionState).toBe(
    expectedAccessTokenValues?.sessionState
  );
  expect(accessToken?.scope).toBe(expectedAccessTokenValues?.scope);
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
