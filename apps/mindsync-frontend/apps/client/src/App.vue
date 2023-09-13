<script setup lang="ts">
import { type Component, onMounted, provide, shallowRef } from 'vue';
import { type RouteLocationNormalized, RouterView } from 'vue-router';
import { useAuthStore, useXsrfTokenStore } from '@/stores';
import LoginService from '@/authentication/application/LoginService';
import AccountService from '@/authentication/application/AccountService';
import RefreshTokenService from '@/authentication/application/RefreshTokenService';
import Layouts from '@templates/Layouts';
import router from '@/router';

const authStore = useAuthStore();
const xsrfTokenStore = useXsrfTokenStore();
provide('loginService', new LoginService(authStore));
const refreshTokenService = new RefreshTokenService(authStore);
provide('refreshTokenService', refreshTokenService);
const accountService = new AccountService(authStore, refreshTokenService);
provide('accountService', accountService);

const layout: Component | string = shallowRef('div');

const determineLayout = (to: RouteLocationNormalized) => {
  layout.value = Layouts[to.meta.layout] || 'div';
};

const isPublicRoute = (to: RouteLocationNormalized): boolean => {
  return to.matched.some((record) => record.meta.isPublic);
};

const fetchAccountInfo = (to: RouteLocationNormalized, callBack: Function) => {
  accountService.retrieveAccountFromServer().then((account) => {
    if (account) {
      callBack();
    } else {
      authStore.authenticate(to.path);
    }
  });
};

const checkAuthorityAndNavigate = async (to: RouteLocationNormalized) => {
  if (to.meta?.authorities && to.meta.authorities.length > 0) {
    if (!(await authStore.hasAnyAuthority(to.meta.authorities))) {
      await router.push({ name: 'forbidden' });
    }
  }
};

router.beforeResolve(async (to, _, next) => {
  determineLayout(to);
  if (isPublicRoute(to)) {
    next();
    return;
  }
  if (!authStore.isAuthenticated) {
    await authStore.authenticate(to.path);
  } else {
    fetchAccountInfo(to, next);
  }
  await checkAuthorityAndNavigate(to);
  next();
});
provide('app:layout', layout);

onMounted(() => {
  xsrfTokenStore.init();
  if (authStore.isAuthenticated) {
    fetchAccountInfo(router.currentRoute.value, () => {
      router.push(authStore.url || '/');
    });
  }
});
</script>

<template>
  <component :is="layout || 'div'">
    <RouterView />
  </component>
</template>
