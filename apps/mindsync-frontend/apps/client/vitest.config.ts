import { fileURLToPath } from 'node:url';

import { defineConfig, mergeConfig } from 'vite';
import { configDefaults } from 'vitest/config';

import { defineAppConfig } from './vite.config';

export default mergeConfig(
  defineAppConfig(),
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
