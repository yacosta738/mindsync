import { defineStore } from 'pinia';
// eslint-disable-next-line import/extensions
import router from '@/router';
import type { AccessToken } from '@/authentication/domain/AccessToken';

export type AuthStore = ReturnType<typeof useAuthStore>;

export interface AccountStateStorable {
  token?: AccessToken;
  returnUrl?: string;
}

export const defaultAccountState: AccountStateStorable = {
  token: null,
  returnUrl: '/',
};
export const useAuthStore = defineStore({
  id: 'auth',
  state: (): AccountStateStorable => ({
    ...defaultAccountState,
  }),
  getters: {
    accessToken: (state) => state.token,
    returnUrl: (state) => state.returnUrl,
    isAuthenticated: (state) => !!state.token,
  },
  actions: {
    async setAccessToken(accessToken: AccessToken) {
      try {
        // update pinia state
        this.token = accessToken;

        // redirect to previous url or default to home page
        await router.push(this.returnUrl || '/');
      } catch (error) {
        // TODO: use alert store
        // const alertStore = useAlertStore();
        // alertStore.error(error);
        console.error(error);
      }
    },
    async authenticate(returnUrl?: string) {
      try {
        this.returnUrl = returnUrl;
        await router.push('/login');
      } catch (error) {
        console.error(error);
      }
    },
  },
});
