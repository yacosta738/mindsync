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

let loginService: LoginService;
let accountService: AccountService;
const mockedFetch = vi.fn();

describe('LoginView', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
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
    const mockAccessToken: AccessToken = {
      token: 'test',
      expiresIn: 3600,
      refreshToken: 'test',
      refreshExpiresIn: 3600,
      tokenType: 'test',
      notBeforePolicy: 3600,
      sessionState: 'test',
      scope: 'test',
    };
    const headers = new Headers();
    headers.append('Content-Type', 'application/json');
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
        username: 'test@supercompany.com',
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
});
