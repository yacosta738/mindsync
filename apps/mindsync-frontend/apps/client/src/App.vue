<script setup lang="ts">
import { onMounted, provide, shallowRef, type Component } from 'vue';
import { RouterLink, RouterView } from 'vue-router';
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
router.beforeResolve(async (to, from, next) => {
  layout.value = Layouts[to.meta.layout] || 'div';
  const isPublicRoute = to.matched.some((record) => record.meta.isPublic);
  if (isPublicRoute) {
    next();
    return;
  }
  if (!authStore.isAuthenticated) {
    await authStore.authenticate(to.path);
  } else {
    accountService.retrieveAccountFromServer().then((account) => {
      if (account) {
        next();
      } else {
        authStore.authenticate(to.path);
      }
    });
  }
  if (to.meta?.authorities && to.meta.authorities.length > 0) {
    if (!(await authStore.hasAnyAuthority(to.meta.authorities))) {
      await router.push({ name: 'forbidden' });
    }
  }
  next();
});
provide('app:layout', layout);

onMounted(() => {
  xsrfTokenStore.init();
  if (authStore.isAuthenticated) {
    accountService.retrieveAccountFromServer().then((account) => {
      if (account) {
        router.push(authStore.url || '/');
      } else {
        authStore.authenticate();
      }
    });
  }
});
</script>

<template>
  <component :is="layout || 'div'">
    <RouterView />
  </component>
</template>
