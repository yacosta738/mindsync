import { defineStore } from 'pinia';
import Cookies from 'js-cookie';

export type XsrfTokenStore = ReturnType<typeof useXsrfTokenStore>;
const COOKIE_NAME = 'XSRF-TOKEN';

/**
 * Interface representing the state of an XSRF token.
 * @interface
 */
export interface XsrfTokenState {
  xsrfToken?: string;
}

/**
 * Gets the XSRF token from the cookie.
 *
 * @returns {string|undefined} The XSRF token or undefined if not found.
 */
const getXsrfTokenFromCookie = () => {
  const xsrfCookie = Cookies.get(COOKIE_NAME);
  return xsrfCookie || undefined;
};

/**
 * Extracts the XSRF token from the given cookie header.
 * @param {string} xsrfCookieHeader - The cookie header string.
 * @returns {string} The XSRF token extracted from the cookie header, or null if not found.
 */
const extractXsrfTokenFromCookie = (xsrfCookieHeader: string) => {
  const xsrfCookie = xsrfCookieHeader
    .split(';')
    .find((cookie) => cookie.trim().startsWith(COOKIE_NAME));
  return xsrfCookie?.split('=')[1];
};

export const useXsrfTokenStore = defineStore({
  id: 'xsrfToken',
  state: (): XsrfTokenState => ({
    xsrfToken: getXsrfTokenFromCookie(),
  }),
  getters: {
    securityXsrfToken: (state) => state.xsrfToken,
  },
  actions: {
    async init() {
      // Try to get the XSRF token from the cookie
      const xsrfCookie = Cookies.get(COOKIE_NAME);
      this.xsrfToken = xsrfCookie || undefined;

      // If the XSRF token is not found, make a request to obtain it
      if (!this.xsrfToken) {
        try {
          const response = await fetch('/api/check-health', {
            method: 'GET',
            credentials: 'include', // Enable cookies in the request
          });
          // Extract the XSRF token from the cookie in the response
          const xsrfCookieHeader = response.headers.get('Set-Cookie');
          if (xsrfCookieHeader) {
            this.xsrfToken = extractXsrfTokenFromCookie(xsrfCookieHeader);
            Cookies.set(COOKIE_NAME, this.xsrfToken || '');
          }
        } catch (error) {
          console.error('Error retrieving the XSRF token:', error);
        }
      }
    },
  },
});
