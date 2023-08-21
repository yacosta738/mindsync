import { defineStore } from 'pinia';
// eslint-disable-next-line import/extensions
import router from '@/router';
import type { AccessToken } from '@/authentication/domain/AccessToken';

export type AuthStore = ReturnType<typeof useAuthStore>;

export interface AccountStateStorable {
  logon: boolean;
  token: null | AccessToken;
  authenticated: boolean;
  profilesLoaded: boolean;
  activeProfiles: string;
  returnUrl?: string;
}

export const defaultAccountState: AccountStateStorable = {
  logon: false,
  token: null,
  authenticated: false,
  profilesLoaded: false,
  activeProfiles: '',
  returnUrl: '/',
};
export const useAuthStore = defineStore({
  id: 'auth',
  state: (): AccountStateStorable => ({
    ...defaultAccountState,
  }),
  getters: {
    accessToken: (state) => state.token,
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
  },
});
