import { defineConfig } from 'astro/config';

import sitemap from '@astrojs/sitemap';
import tailwind from '@astrojs/tailwind';
import astroI18next from 'astro-i18next';

const DEV_PORT = 2121;

// https://astro.build/config
export default defineConfig({
    site: process.env.CI
        ? 'https://themesberg.github.io'
        : `http://localhost:${DEV_PORT}`,
    server: {
        /* Dev. server only */
        port: DEV_PORT,
    },
    output: 'static',
    integrations: [
        sitemap(),
        tailwind(),
        astroI18next()
    ],
});