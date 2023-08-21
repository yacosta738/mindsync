<script setup lang="ts">
import { provide, shallowRef } from 'vue';
import { RouterLink, RouterView } from 'vue-router';
import Layouts from '@templates/Layouts';
import router from '@/router';
import { useAuthStore } from '@/stores';
import LoginService from '@/authentication/LoginService';

const authStore = useAuthStore();
provide('loginService', new LoginService(authStore));

const layout = shallowRef('div');
router.afterEach((to) => {
  layout.value = Layouts[to.meta.layout] || 'div';
});

provide('app:layout', layout);
</script>

<template>
  <component :is="layout || 'div'">
    <RouterView />
  </component>
</template>
