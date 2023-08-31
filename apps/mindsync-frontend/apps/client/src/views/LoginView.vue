<script setup lang="ts">
import { inject, onMounted, ref } from 'vue';
import type { Ref } from 'vue';
import LoginService from '@/authentication/LoginService';
import type AccountService from '@/authentication/AccountService';
import { useVuelidate } from '@vuelidate/core';
import { email, minLength, required } from '@vuelidate/validators';
import router from '@/router';
import { type AuthStore, useAuthStore } from '@/stores';

const authStore: AuthStore = useAuthStore();
const emailOrUsername: Ref<string> = ref('');
const password: Ref<string> = ref('');
const rememberMe: Ref<boolean> = ref(false);

const loginService = inject<LoginService>('loginService');
const accountService = inject<AccountService>('accountService');
onMounted(async () => {
  if (await loginService?.isAuthenticated()) {
    console.log(
      `User is already authenticated, redirecting to home page ${loginService?.isAuthenticated()}`
    );
    accountService
      ?.getAccount()
      .then((account) => {
        if (account) {
          router.push(authStore.url || '/');
        } else {
          loginService?.logout();
        }
      })
      .catch((error) => {
        loginService?.logout();
      });
  }
});
const rules = {
  emailOrUsername: {
    required,
    email,
  },
  password: {
    required,
    minLength: minLength(8),
  },
};

const v$ = useVuelidate(rules, { emailOrUsername, password });

const onSubmit = () => {
  v$.value.$touch();
  if (v$.value.$error) {
    return;
  }
  loginService?.login(emailOrUsername.value, password.value, rememberMe.value);
};
</script>
<template>
  <section class="bg-gray-50 dark:bg-gray-900">
    <div
      class="mx-auto flex flex-col items-center justify-center px-6 py-8 md:h-screen lg:py-0"
    >
      <a
        href="#"
        class="mb-6 flex items-center text-2xl font-semibold text-gray-900 dark:text-white"
      >
        <img
          class="mr-2 h-8 w-8"
          src="https://flowbite.s3.amazonaws.com/blocks/marketing-ui/logo.svg"
          alt="logo"
        />
        MindSync
      </a>
      <div
        class="w-full rounded-lg bg-white shadow dark:border dark:border-gray-700 dark:bg-gray-800 sm:max-w-md md:mt-0 xl:p-0"
      >
        <div class="space-y-4 p-6 sm:p-8 md:space-y-6">
          <h1
            class="text-xl font-bold leading-tight tracking-tight text-gray-900 dark:text-white md:text-2xl"
          >
            Sign in to your account
          </h1>
          <form class="space-y-4 md:space-y-6" @submit.prevent="onSubmit">
            <div :class="{ error: v$.emailOrUsername.$errors.length }">
              <label
                for="email"
                class="mb-2 block text-sm font-medium text-gray-900 dark:text-white"
                >Your email</label
              >
              <input
                id="email"
                v-model="emailOrUsername"
                type="email"
                name="email"
                class="focus:ring-primary-600 focus:border-primary-600 block w-full rounded-lg border border-gray-300 bg-gray-50 p-2.5 text-gray-900 dark:border-gray-600 dark:bg-gray-700 dark:text-white dark:placeholder-gray-400 dark:focus:border-blue-500 dark:focus:ring-blue-500 sm:text-sm"
                placeholder="name@company.com"
                required
              />
            </div>
            <div :class="{ error: v$.password.$errors.length }">
              <label
                for="password"
                class="mb-2 block text-sm font-medium text-gray-900 dark:text-white"
                >Password</label
              >
              <input
                id="password"
                v-model="password"
                type="password"
                name="password"
                placeholder="••••••••"
                class="focus:ring-primary-600 focus:border-primary-600 block w-full rounded-lg border border-gray-300 bg-gray-50 p-2.5 text-gray-900 dark:border-gray-600 dark:bg-gray-700 dark:text-white dark:placeholder-gray-400 dark:focus:border-blue-500 dark:focus:ring-blue-500 sm:text-sm"
                required
              />
            </div>
            <div class="flex items-center justify-between">
              <div class="flex items-start">
                <div class="flex h-5 items-center">
                  <input
                    id="remember"
                    v-model="rememberMe"
                    aria-describedby="remember"
                    type="checkbox"
                    class="focus:ring-3 focus:ring-primary-300 dark:focus:ring-primary-600 h-4 w-4 rounded border border-gray-300 bg-gray-50 dark:border-gray-600 dark:bg-gray-700 dark:ring-offset-gray-800"
                  />
                </div>
                <div class="ml-3 text-sm">
                  <label for="remember" class="text-gray-500 dark:text-gray-300"
                    >Remember me</label
                  >
                </div>
              </div>
              <a
                href="#"
                class="text-primary-600 dark:text-primary-500 text-sm font-medium hover:underline"
                >Forgot password?</a
              >
            </div>
            <button
              type="submit"
              class="bg-primary-600 hover:bg-primary-700 focus:ring-primary-300 dark:bg-primary-600 dark:hover:bg-primary-700 dark:focus:ring-primary-800 w-full rounded-lg px-5 py-2.5 text-center text-sm font-medium text-white focus:outline-none focus:ring-4"
            >
              Sign in
            </button>
            <p class="text-sm font-light text-gray-500 dark:text-gray-400">
              Don’t have an account yet?
              <a
                href="/register"
                class="text-primary-600 dark:text-primary-500 font-medium hover:underline"
                >Sign up</a
              >
            </p>
          </form>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped></style>
