<script setup lang="ts">
import NavBarStacked from '@organisms/NavBarStacked.vue';
import SideBar from '@organisms/SideBar.vue';
</script>

<template>
  <div class="bg-gray-50 antialiased dark:bg-gray-900">
    <NavBarStacked />
    <SideBar />
    <main class="h-auto p-4 pt-20 md:ml-64">
      <slot></slot>
    </main>
  </div>
</template>

<style scoped></style>
