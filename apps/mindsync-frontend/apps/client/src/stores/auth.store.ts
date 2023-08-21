import { defineStore } from 'pinia';
import router from '@/router';
import { FetchWebClient } from '@/utils/webclient';

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
  returnUrl: '/'
};

interface LoginRequest {
  username: string;
  password: string;
}

interface AccessToken {
  token: string;
  expiresIn: number;
  refreshToken: string;
  refreshExpiresIn: number;
  tokenType: string;
  notBeforePolicy: number;
  sessionState: string;
  scope: string;
}

const webClient = new FetchWebClient<AccessToken, LoginRequest>();

const TOKEN_KEY = 'accessToken';

const storeToken = async (accessToken: AccessToken, rememberMe = true) => {
    if (rememberMe) {
        localStorage.setItem(TOKEN_KEY, JSON.stringify(accessToken));
    } else {
        sessionStorage.setItem(TOKEN_KEY, JSON.stringify(accessToken));
    }
}
export const useAuthStore = defineStore({
  id: 'auth',
  state: (): AccountStateStorable => ({
    ...defaultAccountState
  }),
  getters: {
    accessToken: (state) => state.token,
  },
  actions: {
    async login(username: string, password: string, rememberMe: boolean) {
      try {
        const accessToken = await webClient.post(`api/login`, {
          username,
          password,
        });

        // update pinia state
        this.token = accessToken;

        await storeToken(accessToken, rememberMe);

        // redirect to previous url or default to home page
        await router.push(this.returnUrl || '/');
      } catch (error) {
        // const alertStore = useAlertStore();
        // alertStore.error(error);
        console.error(error);
      }
    },
    logout() {
      this.token = null;
      localStorage.removeItem(TOKEN_KEY);
      router.push('/login').then(r => console.log(r));
    },
  },
});
