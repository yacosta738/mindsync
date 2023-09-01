<script setup lang="ts">
import { onMounted, provide, shallowRef, type Component } from 'vue';
import { RouterLink, RouterView } from 'vue-router';
import { useAuthStore } from '@/stores';
import LoginService from '@/authentication/LoginService';
import AccountService from '@/authentication/AccountService';
import RefreshTokenService from '@/authentication/RefreshTokenService';
import Layouts from '@templates/Layouts';
import router from '@/router';

const authStore = useAuthStore();
provide('loginService', new LoginService(authStore));
const refreshTokenService = new RefreshTokenService(authStore);
provide('refreshTokenService', refreshTokenService);
const accountService = new AccountService(authStore, refreshTokenService);
provide('accountService', accountService);

const layout: Component | string = shallowRef('div');
router.afterEach((to) => {
  layout.value = Layouts[to.meta.layout] || 'div';
});

provide('app:layout', layout);

onMounted(() => {
  console.log('Checking if user is authenticated');
  if (authStore.isAuthenticated) {
    console.log(
      `User is already authenticated, redirecting to home page ${authStore.isAuthenticated}`
    );
    accountService.retrieveAccountFromServer().then((account) => {
      if (account) {
        router.push(authStore.url || '/');
      } else {
        authStore.authenticate();
      }
    });
  }
});
router.beforeResolve(async (to, from, next) => {
  const isPublicRoute = to.matched.some((record) => record.meta.isPublic);
  if (isPublicRoute) {
    next();
    return;
  }
  if (!authStore.isAuthenticated) {
    console.log('User is not authenticated, redirecting to login page');
    await authStore.authenticate(to.fullPath);
  } else {
    console.log('User is authenticated, proceeding to requested page');
    accountService.retrieveAccountFromServer().then((account) => {
      if (account) {
        console.log('User is authenticated, proceeding to requested page');
        next();
      } else {
        console.log('User is not authenticated, redirecting to login page');
        authStore.authenticate(to.fullPath);
      }
    });
  }
  if (to.meta?.authorities && to.meta.authorities.length > 0) {
    console.log('Checking authorities', to.meta.authorities);
    if (!(await authStore.hasAnyAuthority(to.meta.authorities))) {
      await router.push({ name: 'forbidden' });
    }
  }
  next();
});
</script>

<template>
  <component :is="layout || 'div'">
    <RouterView />
  </component>
</template>
