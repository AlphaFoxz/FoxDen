import {dirname} from 'node:path';
import {fileURLToPath} from 'node:url';
import {defineConfig, loadEnv} from 'vite';
import vue from '@vitejs/plugin-vue';
import vueDevTools from 'vite-plugin-vue-devtools';

// https://vite.dev/config/
export default defineConfig(({mode}) => {
  const env = loadEnv(mode, dirname(fileURLToPath(import.meta.url)));
  return {
    plugins: [
      vue(),
      vueDevTools(),
    ],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('src', import.meta.url)),
      },
    },
    server: {
      host: '0.0.0.0',
      port: Number(env.VITE_APP_PORT),
      open: true,
      proxy: {
        [env.VITE_APP_BASE_API]: {
          target: 'http://127.0.0.1:11003',
          // Target: 'http://192.168.198.98:11003',
          // target: 'http://115.190.23.242:11003',
          changeOrigin: true,
          ws: true,
          rewrite: path => path.replace(new RegExp('^' + env.VITE_APP_BASE_API), ''),
        },
      },
    },
  };
});

