/// <reference types="vite/client" />

interface ImportMetaEnv {
  VITE_APP_TITLE: string;
  VITE_APP_PORT: number;
  VITE_APP_BASE_API: string;
  VITE_APP_BASE_URL: string;
  VITE_APP_CONTEXT_PATH: string;
  VITE_APP_MONITOR_ADMIN: string;
  VITE_APP_SNAILJOB_ADMIN: string;
  VITE_APP_ENV: string;
  VITE_APP_ENCRYPT: string;
  VITE_APP_RSA_PUBLIC_KEY: string;
  VITE_APP_RSA_PRIVATE_KEY: string;
  VITE_APP_CLIENT_ID: string;
  VITE_APP_WEBSOCKET: string;
  VITE_APP_SSE: string;
}
