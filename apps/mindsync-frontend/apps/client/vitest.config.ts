import { fileURLToPath } from 'node:url';

import { defineConfig, mergeConfig } from 'vite';
import { configDefaults } from 'vitest/config';

import viteConfig from './vite.config';

export default mergeConfig(
  viteConfig,
  defineConfig({
    test: {
      environment: 'jsdom',
      exclude: [...configDefaults.exclude, 'e2e/*', 'tests/integration/*'],
      root: fileURLToPath(new URL('./', import.meta.url)),
      transformMode: {
        web: [/\.[jt]sx$/],
      },
    },
  })
);
