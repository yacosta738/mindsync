import 'vue-router';

// To ensure it is treated as a module, add at least one `export` statement
export {};

declare module 'vue-router' {
  interface RouteMeta {
    // is optional
    isAdmin?: boolean;
    isPublic?: boolean;
    error403?: boolean;
    error404?: boolean;
    authorities?: string[];
    // must be declared by every route
    layout: 'LayoutStacked' | 'SimpleLayout';
  }
}
