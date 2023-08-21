declare module '*.vue' {
  import { DefineComponent } from 'vue';
  const component: DefineComponent & never;
  export default component;
}
