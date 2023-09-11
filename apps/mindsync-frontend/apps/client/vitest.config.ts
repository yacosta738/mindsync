/// <reference types="vitest" />
import { fileURLToPath } from 'node:url';

import { defineConfig, mergeConfig } from 'vite';
import { configDefaults } from 'vitest/config';

import viteConfig from './vite.config';

export default defineConfig((configEnv) =>
  mergeConfig(
    viteConfig({
      mode: configEnv.mode,
    }),
    defineConfig({
      test: {
        environment: 'jsdom',
        exclude: [...configDefaults.exclude, 'e2e/*', 'tests/integration/*'],
        root: fileURLToPath(new URL('./', import.meta.url)),
      },
    })
  )
);
