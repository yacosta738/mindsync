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
provide('accountService', new AccountService(authStore));

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
  const isPublicRoute = to.matched.some((record) => record.meta.isPublic);
  if (isPublicRoute) {
    next();
    return;
  }
  if (!authStore.isAuthenticated) {
    await authStore.authenticate(to.fullPath);
  }
  if (to.meta?.authorities && to.meta.authorities.length > 0) {
    // TODO: implement logic for checking if user has access to the route
    // if (!authStore.hasAnyAuthority(to.meta.authorities)) {
    //   router.push({ name: 'accessdenied' });
    // }
  }
  next();
});
</script>

<template>
  <component :is="layout || 'div'">
    <RouterView />
  </component>
</template>
