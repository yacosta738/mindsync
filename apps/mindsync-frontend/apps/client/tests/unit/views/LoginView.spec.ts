import { flushPromises, mount } from '@vue/test-utils';
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
import { nextTick } from 'vue';
import User from '../../../src/authentication/domain/User';

let loginService: LoginService;
let accountService: AccountService;
const mockedFetch = vi.fn();
const mockAccessToken: AccessToken = createMockAccessToken();
const user = createMockUser();
let headers: Headers;

describe('Login View Component', () => {
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

  it('should validate the email field', async () => {
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
    await nextTick();
    const email = wrapper.find('#email');
    await email.setValue('invalid email');
    await email.trigger('blur');
    await email.trigger('focusout');
    await wrapper.find('#password').setValue('12345678');
    await wrapper.find('form').trigger('submit');
    const emailErrorElement = wrapper.find('#email-error');
    expect(emailErrorElement.exists()).toBeTruthy();
    expect(emailErrorElement.text()).toBe(
      'The email field must be a valid email'
    );
    expect(wrapper.find('#email-box').classes()).toContain('error');
  });

  it('should validate the password field', async () => {
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
    await nextTick();
    await wrapper.find('#email').setValue('test@test.com');
    const password = wrapper.find('#password');
    await password.setValue('123');
    await password.trigger('blur');
    await password.trigger('focusout');
    await wrapper.find('form').trigger('submit');
    const passwordErrorElement = wrapper.find('#password-error');
    expect(passwordErrorElement.exists()).toBeTruthy();
    expect(passwordErrorElement.text()).toBe(
      'The password field must be valid'
    );
    expect(wrapper.find('#password-box').classes()).toContain('error');
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
    await flushPromises();
    await nextTick();
    const authenticated = authStore.isAuthenticated;
    expect(authenticated).toBeTruthy();
    const userIdentity: User = authStore.account ?? {
      id: '',
      username: '',
      email: '',
      firstName: '',
      lastName: '',
      authorities: [],
    };
    compareUserAttributes(userIdentity, user);
    headers.append('Authorization', `Bearer ${mockAccessToken.token}`);
    expect(mockedFetch).toHaveBeenLastCalledWith('api/account', {
      headers: headers,
    });
    // email and password should be empty after login
    expect(wrapper.find('#email').text()).toBe('');
    expect(wrapper.find('#password').text()).toBe('');
  });

  it('should use the refresh token if the access token is expired', async () => {
    const expiredAccessToken = createMockAccessToken();
    expiredAccessToken.expiresIn = 0;
    mockedFetch
      .mockResolvedValueOnce(
        createAFetchMockResponse(401, { message: 'Unauthorized' })
      )
      .mockResolvedValueOnce(createAFetchMockResponse(200, expiredAccessToken))
      .mockResolvedValueOnce(createAFetchMockResponse(200, user));
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

    await flushPromises();
    await nextTick();
    const authenticated = authStore.isAuthenticated;
    expect(authenticated).toBeTruthy();

    const userIdentity: User = authStore.account ?? {
      id: '',
      username: '',
      email: '',
      firstName: '',
      lastName: '',
      authorities: [],
    };
    compareUserAttributes(userIdentity, user);
    headers.append('Authorization', `Bearer ${mockAccessToken.token}`);
    expect(mockedFetch).toHaveBeenLastCalledWith('api/account', {
      headers: headers,
    });
    // email and password should be empty after login
    expect(wrapper.find('#email').text()).toBe('');
    expect(wrapper.find('#password').text()).toBe('');
  });

  it('should logout if the refresh token is expired', async () => {
    const expiredAccessToken = createMockAccessToken();
    expiredAccessToken.expiresIn = 0;
    mockedFetch
      .mockResolvedValueOnce(
        createAFetchMockResponse(401, { message: 'Unauthorized' })
      )
      .mockResolvedValueOnce(
        createAFetchMockResponse(401, { message: 'Unauthorized' })
      );
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

    await flushPromises();
    await nextTick();
    const authenticated = authStore.isAuthenticated;
    expect(authenticated).toBeFalsy();
    expect(authStore.accessToken).toBeNull();
    expect(authStore.account).toBeNull();
    expect(authStore.rememberMe).toBeFalsy();
    expect(authStore.returnUrl).toBe('/');
    expect(wrapper.find('#email').text()).toBe('');
    expect(wrapper.find('#password').text()).toBe('');
  });
});
