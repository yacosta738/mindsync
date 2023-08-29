<script setup lang="ts">
import { onMounted, provide, shallowRef, type Component } from 'vue';
import { RouterLink, RouterView } from 'vue-router';
import { useAuthStore } from '@/stores';
import LoginService from '@/authentication/LoginService';
import AccountService from '@/authentication/AccountService';
import Layouts from '@templates/Layouts';
import router from '@/router';

const authStore = useAuthStore();
provide('loginService', new LoginService(authStore));
const accountService = new AccountService(authStore);
provide('accountService', accountService);

const layout: Component | string = shallowRef('div');
router.afterEach((to) => {
  layout.value = Layouts[to.meta.layout] || 'div';
});

provide('app:layout', layout);

// onMounted(() => {
//   // check if user has a valid token stored in local storage or session storage and if so, authenticate the user
//   authStore.authenticate();
// });
router.beforeResolve(async (to, from, next) => {
  console.log('Navigating to route', to);
  const isPublicRoute = to.matched.some((record) => record.meta.isPublic);
  if (isPublicRoute) {
    next();
    return;
  }
  if (!authStore.isAuthenticated) {
    console.log('User is not authenticated, redirecting to login page');
    await authStore.authenticate(to.fullPath);
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
