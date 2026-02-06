import {fileURLToPath} from 'node:url';
import {mergeConfig, defineConfig, configDefaults} from 'vitest/config';
import viteConfigFn from './vite.config';

// Vite.config.ts exports a function, need to call it first
const viteConfig = viteConfigFn({mode: 'test', command: 'serve'});

export default mergeConfig(
  viteConfig,
  defineConfig({
    test: {
      environment: 'jsdom',
      exclude: [...configDefaults.exclude, 'e2e/**'],
      root: fileURLToPath(new URL('./', import.meta.url)),
    },
  }),
);
