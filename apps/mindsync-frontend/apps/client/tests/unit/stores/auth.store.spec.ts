import { beforeEach, describe, expect, it } from 'vitest';
import { mount } from '@vue/test-utils';
import { createPinia, setActivePinia } from 'pinia';
import { AccessToken } from '../../../src/authentication/domain/AccessToken';
import { useAuthStore } from '../../../src/stores';
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

// const mockRouter = {
//   push: vitest.fn(),
// };
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
});
