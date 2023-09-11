import { flushPromises, shallowMount } from '@vue/test-utils';
import { describe, expect, it, vi } from 'vitest';
import { createTestingPinia, TestingOptions } from '@pinia/testing';
import UserMenu from '@/components/molecules/UserMenu.vue';
import { createMockUser } from '../../UserMocks';
import {
  AccountStateStorable,
  AuthStore,
  useAuthStore,
} from '../../../../src/stores';
import { AccessToken } from '../../../../src/authentication/domain/AccessToken';
import { createMockAccessToken } from '../../AccessTokenMocks';
import { nextTick } from 'vue';

const user = createMockUser();
const mockAccessToken: AccessToken = createMockAccessToken();

describe('UserMenu', () => {
  function factory(
    options?: TestingOptions,
    useStore: () => AuthStore = useAuthStore
  ) {
    const wrapper = shallowMount(UserMenu, {
      global: {
        plugins: [createTestingPinia(options)],
      },
    });
    const authStore = useStore();
    return {
      wrapper,
      authStore,
    };
  }
  const initialAccountState: AccountStateStorable = {
    token: mockAccessToken,
    rememberMe: false,
    userIdentity: user,
    returnUrl: '/',
  };
  it('renders properly', async () => {
    const { wrapper, authStore } = factory({
      initialState: {
        auth: initialAccountState,
      },
      createSpy: vi.fn,
    });
    await nextTick();
    await flushPromises();
    const componentText = wrapper.text();
    console.log(componentText);
    expect(componentText).toContain(user.firstname);
    expect(componentText).toContain(user.lastname);
    expect(componentText).toContain(user.email);
    expect(componentText).toContain('My profile');
    expect(componentText).toContain('Account settings');
    expect(componentText).toContain('Sign out');

    expect(authStore.isAuthenticated).toBe(true);
    expect(authStore.account).toStrictEqual(user);
  });

  it('logout button works properly', async () => {
    const { wrapper, authStore } = factory({
      initialState: {
        auth: initialAccountState,
      },
      createSpy: vi.fn,
    });

    await nextTick();
    const logoutButton = wrapper.find('#logout-button');
    expect(logoutButton.exists()).toBe(true);
    expect(logoutButton.text()).toContain('Sign out');
    await logoutButton.trigger('click');
    await flushPromises();
    expect(authStore.logout).toHaveBeenCalledTimes(1);
  });
});
