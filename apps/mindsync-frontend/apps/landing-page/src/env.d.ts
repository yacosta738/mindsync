/// <reference types="@astrojs/image/client" />

// https://docs.astro.build/en/guides/environment-variables/#intellisense-for-typescript
interface ImportMetaEnv {
  readonly SITE: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
