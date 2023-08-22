<script setup lang="ts">
import { provide, shallowRef } from 'vue';
import { RouterLink, RouterView } from 'vue-router';
import { useAuthStore } from '@/stores';
import LoginService from '@/authentication/LoginService';
import Layouts from '@templates/Layouts';
import router from '@/router';

const authStore = useAuthStore();
provide('loginService', new LoginService(authStore));

const layout = shallowRef('div');
router.afterEach((to) => {
  layout.value = Layouts[to.meta.layout] || 'div';
});

provide('app:layout', layout);

router.beforeResolve(async (to, from, next) => {
  // implement logic for checking if user is authenticated and if has access to the route
  if (!authStore.isAuthenticated) {
    await authStore.authenticate(to.fullPath);
  }
  if (to.meta?.authorities && to.meta.authorities.length > 0) {
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
