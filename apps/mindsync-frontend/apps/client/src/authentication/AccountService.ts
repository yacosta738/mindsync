import type { WebClient } from '@/utils/WebClient';
import FetchWebClient from '@/utils/WebClient';
import type User from '@/authentication/domain/User';
import type { AuthStore } from '@/stores';

export default class AccountService {
  private webClient: WebClient;

  constructor(private authStore: AuthStore) {
    this.webClient = new FetchWebClient('');
  }

  private url = `api/account`;

  async getAccount(): Promise<User> {
    console.log('getAccount called in AccountService');
    return this.webClient
      .get<User>(this.url)
      .then((response) => {
        console.log('get account response', response);
        this.authStore.setIdentity(response);
        return response;
      })
      .catch((error) => {
        console.log(error);
        this.authStore.logout();
        return error;
      });
  }
}
