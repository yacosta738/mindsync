<script setup lang="ts">
import { provide, shallowRef } from 'vue';
import { RouterLink, RouterView } from 'vue-router';
import layouts from '@templates/'
import router from '@/router';

const layout = shallowRef('div');
router.afterEach((to) => {
  layout.value = layouts[to.meta.layout] || 'div'
});

provide('app:layout', layout);
</script>

<template>
  <component :is="layout || 'div'">
    <RouterView />
  </component>
</template>
