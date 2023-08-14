import { defineWorkspace, configDefaults } from 'vitest/config';
import { fileURLToPath } from 'node:url';
import Vue from '@vitejs/plugin-vue';

// defineWorkspace provides a nice type hinting DX
export default defineWorkspace([
    'apps/*',
    'libs/*',
    {
        extends: './vite.config.js',
        plugins: [Vue()],
        test: {
            include: ['tests/unit/**/*.{browser}.test.{ts,js}'],
            exclude: [...configDefaults.exclude, 'tests/integration/*'],
            name: 'js-dom',
            environment: 'jsdom',
            root: fileURLToPath(new URL('./', import.meta.url)),
            testTransformMode: {
                web: [/\.[jt]sx$/],
            },
        },
    },
    {
        test: {
            include: ['tests/unit/**/*.{node}.test.{ts,js}'],
            exclude: [...configDefaults.exclude, 'tests/integration/*'],
            name: 'node',
            environment: 'node',
        },
    },
]);
