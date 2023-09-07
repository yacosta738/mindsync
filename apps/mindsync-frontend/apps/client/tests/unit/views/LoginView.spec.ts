import { mount } from '@vue/test-utils';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { createPinia, setActivePinia } from 'pinia';
import LoginView from '@views/LoginView.vue';
import { useAuthStore } from '../../../src/stores';
import LoginService from '../../../src/authentication/application/LoginService';
import AccountService from '../../../src/authentication/application/AccountService';
import RefreshTokenService from '../../../src/authentication/application/RefreshTokenService';
import { createAFetchMockResponse } from '../ResponseMocks';
import { AccessToken } from '../../../src/authentication/domain/AccessToken';
import { LoginRequest } from '../../../src/authentication/domain/LoginRequest';
import { compareUserAttributes, createMockUser } from '../UserMocks';
import { createMockAccessToken } from '../AccessTokenMocks';

let loginService: LoginService;
let accountService: AccountService;
const mockedFetch = vi.fn();
const mockAccessToken: AccessToken = createMockAccessToken();
const user = createMockUser();
let headers: Headers;

describe('LoginView', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
    headers = new Headers();
    headers.append('Content-Type', 'application/json');
    global.fetch = mockedFetch;
    mockedFetch.mockReset();
  });
  it('should render the component correctly', () => {
    const authStore = useAuthStore();
    loginService = new LoginService(authStore);
    const refreshTokenService: RefreshTokenService = new RefreshTokenService(
      authStore
    );
    accountService = new AccountService(authStore, refreshTokenService);
    const wrapper = mount(LoginView, {
      global: {
        provide: {
          authStore: authStore,
          loginService: loginService,
          accountService: accountService,
        },
      },
    });
    wrapper.vm.$nextTick(() => {
      expect(wrapper.find('.bg-gray-50').exists).toBeTruthy();
      expect(wrapper.find('h1').text()).toBe('Sign in to your account');
    });
  });

  it('should validate the email field', () => {
    const authStore = useAuthStore();
    loginService = new LoginService(authStore);
    const refreshTokenService: RefreshTokenService = new RefreshTokenService(
      authStore
    );
    accountService = new AccountService(authStore, refreshTokenService);
    const wrapper = mount(LoginView, {
      global: {
        provide: {
          authStore: authStore,
          loginService: loginService,
          accountService: accountService,
        },
      },
    });
    wrapper.vm.$nextTick(() => {
      wrapper.find('#email').setValue('invalid email');
      wrapper.find('form').trigger('submit');
      const emailErrorElement = wrapper.find('#email-error');
      expect(emailErrorElement.exists()).toBeTruthy();
      expect(emailErrorElement.text()).toBe(
        'The email field must be a valid email'
      );
      expect(wrapper.find('#email-box').classes()).toContain('error');
    });
  });

  it('should validate the password field', () => {
    const authStore = useAuthStore();
    loginService = new LoginService(authStore);
    const refreshTokenService: RefreshTokenService = new RefreshTokenService(
      authStore
    );
    accountService = new AccountService(authStore, refreshTokenService);
    const wrapper = mount(LoginView, {
      global: {
        provide: {
          authStore: authStore,
          loginService: loginService,
          accountService: accountService,
        },
      },
    });
    wrapper.vm.$nextTick(() => {
      wrapper.find('#password').setValue('123');
      wrapper.find('form').trigger('submit');
      const passwordErrorElement = wrapper.find('#password-error');
      expect(passwordErrorElement.exists()).toBeTruthy();
      expect(passwordErrorElement.text()).toBe(
        'The password field must be valid'
      );
      expect(wrapper.find('#password-box').classes()).toContain('error');
    });
  });
  it('should call the login service when the form is submitted', () => {
    mockedFetch.mockResolvedValue(
      createAFetchMockResponse(200, mockAccessToken)
    );
    const authStore = useAuthStore();
    loginService = new LoginService(authStore);
    const refreshTokenService: RefreshTokenService = new RefreshTokenService(
      authStore
    );
    accountService = new AccountService(authStore, refreshTokenService);
    const wrapper = mount(LoginView, {
      global: {
        provide: {
          authStore: authStore,
          loginService: loginService,
          accountService: accountService,
        },
      },
    });
    wrapper.vm.$nextTick(() => {
      const loginServiceSpy = vi.spyOn(loginService, 'login');
      const loginRequest: LoginRequest = {
        username: user.email,
        password: '123456',
      };
      wrapper.find('#email').setValue(loginRequest.username);
      wrapper.find('#password').setValue(loginRequest.password);
      wrapper.find('form').trigger('submit');
      expect(loginServiceSpy).toHaveBeenCalled();
      expect(mockedFetch).toHaveBeenCalledWith('api/login', {
        method: 'POST',
        body: JSON.stringify(loginRequest),
        headers: headers,
      });
    });
  });

  it('should login and retrieve the account if the user is authenticated', async () => {
    mockedFetch.mockResolvedValue(createAFetchMockResponse(200, user));
    const authStore = useAuthStore();
    await authStore.setAccessToken(mockAccessToken);
    loginService = new LoginService(authStore);
    const refreshTokenService: RefreshTokenService = new RefreshTokenService(
      authStore
    );
    accountService = new AccountService(authStore, refreshTokenService);
    const wrapper = mount(LoginView, {
      global: {
        provide: {
          authStore: authStore,
          loginService: loginService,
          accountService: accountService,
        },
      },
    });
    await wrapper.vm.$nextTick(() => {
      const authenticated = authStore.isAuthenticated;
      expect(authenticated).toBeTruthy();
      // wait for the account to be retrieved from the server (after login component is mounted)
      setTimeout(() => {
        const userIdentity = authStore.userIdentity;
        compareUserAttributes(userIdentity, user);
      }, 1000);
      headers.append('Authorization', `Bearer ${mockAccessToken.token}`);
      expect(mockedFetch).toHaveBeenLastCalledWith('api/account', {
        headers: headers,
      });
    });
  });
});
