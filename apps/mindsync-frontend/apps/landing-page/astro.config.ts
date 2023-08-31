import sitemap from '@astrojs/sitemap';
import tailwind from '@astrojs/tailwind';
import { defineConfig } from 'astro/config';
// eslint-disable-next-line import/no-extraneous-dependencies
import astroI18next from 'astro-i18next';

const DEV_PORT = 3000;

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
  integrations: [sitemap(), tailwind(), astroI18next()],
});
