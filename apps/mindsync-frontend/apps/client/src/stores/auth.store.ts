import { defineStore } from 'pinia';
import router from '@/router';
import type { AccessToken } from '@/authentication/domain/AccessToken';
import type { SecureTokenRepository } from '@/authentication/infrastructure/AuthTokenStorage';
import { OAuthTokenManager } from '@/authentication/infrastructure/AuthTokenStorage';
import type User from '@/authentication/domain/User';
import { state } from 'vue-tsc/out/shared';

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

const getStoredToken = (): AccessToken | null => {
  const token =
    localStorageTokenManager.get() || sessionStorageTokenManager.get();
  if (token) {
    return JSON.parse(token);
  }
  return null;
};

export interface AccountStateStorable {
  token?: AccessToken | null;
  rememberMe?: boolean;
  userIdentity?: User | null;
  returnUrl?: string;
}

export const defaultAccountState: AccountStateStorable = {
  token: getStoredToken(),
  rememberMe: false,
  userIdentity: null,
  returnUrl: '/',
};
export const useAuthStore = defineStore({
  id: 'auth',
  state: (): AccountStateStorable => ({
    ...defaultAccountState,
  }),
  getters: {
    account: (state) => state.userIdentity,
    accessToken: (state) => state.token,
    url: (state) => state.returnUrl,
    isAuthenticated: (state) => !!state.token,
    publicApiRoutes: () =>
      router
        .getRoutes()
        .filter((route) => route.meta.isPublic)
        .map((route) => route.path),
    authorities: (state) => state.userIdentity?.authorities,
    sessionActive: (state) => state.rememberMe,
  },
  actions: {
    async setAccessToken(accessToken: AccessToken, rememberMe = false) {
      try {
        // update pinia state
        this.token = accessToken;
        this.rememberMe = rememberMe;
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
        this.userIdentity = user;
      } catch (error) {
        console.error(error);
      }
    },
    async hasAnyAuthority(authorities: string[]) {
      try {
        return this.userIdentity?.authorities.some((authority) =>
          authorities.includes(authority)
        );
      } catch (error) {
        console.error(error);
      }
    },
    async authenticate(returnUrl?: string) {
      try {
        this.returnUrl = returnUrl || '/';
        await router.push({ name: 'login' });
      } catch (error) {
        console.error(error);
      }
    },
    async setSessionActive(sessionActive: boolean) {
      this.rememberMe = sessionActive;
    },
    async logout() {
      try {
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
