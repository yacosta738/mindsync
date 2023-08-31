import { defineStore } from 'pinia';
// eslint-disable-next-line import/extensions
import router from '@/router';
import type { AccessToken } from '@/authentication/domain/AccessToken';
import type { SecureTokenRepository } from '@/utils/AuthTokenStorage';
import { OAuthTokenManager } from '@/utils/AuthTokenStorage';
import type User from '@/authentication/domain/User';

export type AuthStore = ReturnType<typeof useAuthStore>;

const localStorageTokenManager: SecureTokenRepository = new OAuthTokenManager(
  localStorage
);

const sessionStorageTokenManager: SecureTokenRepository = new OAuthTokenManager(
  sessionStorage
);

const storeToken = async (accessToken: AccessToken, rememberMe = true) => {
  if (rememberMe) {
    localStorageTokenManager.set(JSON.stringify(accessToken));
  } else {
    sessionStorageTokenManager.set(JSON.stringify(accessToken));
  }
};

const getStoredToken = async (): Promise<AccessToken | null> => {
  const token =
    localStorageTokenManager.get() || sessionStorageTokenManager.get();
  if (token) {
    return JSON.parse(token);
  }
  return null;
};
export interface AccountStateStorable {
  token?: AccessToken | null;
  userIdentity?: User | null;
  returnUrl?: string;
}

export const defaultAccountState: AccountStateStorable = {
  token: await getStoredToken(),
  userIdentity: null,
  returnUrl: '/',
};
export const useAuthStore = defineStore({
  id: 'auth',
  state: (): AccountStateStorable => ({
    ...defaultAccountState,
  }),
  getters: {
    accessToken: (state) => state.token,
    url: (state) => state.returnUrl,
    isAuthenticated: (state) => !!state.token,
    publicApiRoutes: () =>
      router
        .getRoutes()
        .filter((route) => route.meta.isPublic)
        .map((route) => route.path),
    authorities: (state) => state.userIdentity?.authorities,
  },
  actions: {
    async setAccessToken(accessToken: AccessToken, rememberMe = true) {
      try {
        // update pinia state
        this.token = accessToken;
        // store token in local storage
        await storeToken(accessToken, rememberMe);

        // redirect to previous url or default to home page
        await router.push(this.returnUrl || '/');
      } catch (error) {
        // TODO: use alert store
        // const alertStore = useAlertStore();
        // alertStore.error(error);
        console.error(error);
      }
    },
    async setIdentity(user: User) {
      try {
        console.log('setIdentity called in AuthStore');
        this.userIdentity = user;
      } catch (error) {
        console.error(error);
      }
    },
    async hasAnyAuthority(authorities: string[]) {
      try {
        console.log('hasAnyAuthority called in AuthStore: ', authorities);
        console.log('userIdentity: ', this.userIdentity);
        return this.userIdentity?.authorities.some((authority) =>
          authorities.includes(authority)
        );
      } catch (error) {
        console.error(error);
      }
    },
    async authenticate(returnUrl?: string) {
      try {
        console.log(`Authenticating user... and redirecting to ${returnUrl}`);
        this.returnUrl = returnUrl || '/';
        await router.push({ name: 'login' });
      } catch (error) {
        console.error(error);
      }
    },
    async logout() {
      try {
        console.log('Logging out user...');
        this.token = null;
        this.userIdentity = null;
        this.returnUrl = '/';
        localStorageTokenManager.clear();
        sessionStorageTokenManager.clear();
        await router.push({ name: 'login' });
      } catch (error) {
        console.error(error);
      }
    },
  },
});
