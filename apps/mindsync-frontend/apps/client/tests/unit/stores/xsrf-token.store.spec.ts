import { beforeEach, afterEach, describe, expect, it, vi } from 'vitest';
import { createPinia, setActivePinia } from 'pinia';

import { useXsrfTokenStore } from '../../../src/stores';
import { createAFetchMockResponse } from '../ResponseMocks';
const mockedFetch = vi.fn();
describe('xsrf token store', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
    global.fetch = mockedFetch;
  });
  afterEach(() => {
    mockedFetch.mockReset();
  });

  it('should init and fetch the cookie from the server', async () => {
    const token = '8fc494c9-44ff-4517-a8ef-9494f43fac72';
    const headers = new Headers();
    headers.append('Content-Type', 'text/plain');
    const cookie = `XSRF-TOKEN=${token}; Path=/; HttpOnly; SameSite=Lax; Max-Age=3600`;
    headers.append('Set-Cookie', cookie);
    mockedFetch.mockResolvedValue(createAFetchMockResponse(200, 'OK', headers));
    const xsrfTokenStore = useXsrfTokenStore();
    await xsrfTokenStore.init();
    expect(mockedFetch).toHaveBeenCalledWith('/api/check-health', {
      method: 'GET',
      credentials: 'include', // Enable cookies in the request
    });
    const xsrfToken = xsrfTokenStore.securityXsrfToken;
    expect(xsrfToken).toBe(token);
  });
});
