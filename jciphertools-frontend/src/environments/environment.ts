declare global {
  interface Window {
    __runtimeConfig?: {
      API_BACKEND_URL?: string;
    };
  }
}

const runtimeApiBaseUrl =
  typeof window !== 'undefined' ? window.__runtimeConfig?.API_BACKEND_URL : undefined;

export const environment = {
  production: false,
  apiBaseUrl: runtimeApiBaseUrl ?? 'API_BACKEND_URL_NOT_SET',
};