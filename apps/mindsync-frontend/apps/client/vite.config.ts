import { fileURLToPath, URL } from 'node:url';

import vue from '@vitejs/plugin-vue';
import { defineConfig, loadEnv, UserConfigExport } from 'vite';

export const defineAppConfig = (
  targetUrl: string = `${process.env.VITE_API_URL}/api`
) =>
  defineConfig({
    plugins: [vue()],
    server: {
      proxy: {
        '/api': {
          target: targetUrl,
          changeOrigin: true,
          secure: false,
          rewrite: (path) => path.replace(/^\/api/, ''),
        },
      },
    },
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
        '@assets': fileURLToPath(new URL('./src/assets', import.meta.url)),
        '@components': fileURLToPath(
          new URL('./src/components', import.meta.url)
        ),
        '@views': fileURLToPath(new URL('./src/views', import.meta.url)),
        '@router': fileURLToPath(new URL('./src/router', import.meta.url)),
        '@store': fileURLToPath(new URL('./src/store', import.meta.url)),
        '@atoms': fileURLToPath(
          new URL('./src/components/atoms', import.meta.url)
        ),
        '@molecules': fileURLToPath(
          new URL('./src/components/molecules', import.meta.url)
        ),
        '@organisms': fileURLToPath(
          new URL('./src/components/organisms', import.meta.url)
        ),
        '@templates': fileURLToPath(
          new URL('./src/components/templates', import.meta.url)
        ),
      },
    },
  });

// https://vitejs.dev/config/
export default ({ mode }: { mode: string }): UserConfigExport => {
  Object.assign(process.env, loadEnv(mode, process.cwd(), ''));
  return defineAppConfig(`${process.env.VITE_API_URL}/api`);
};
