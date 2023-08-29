import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores';
import HomeView from '../views/HomeView.vue';
import { Authority } from '@/authentication/domain/Authority';

const About = () => import('../views/AboutView.vue');
const Login = () => import('../views/LoginView.vue');
const Error = () => import('../views/ErrorView.vue');

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { layout: 'LayoutStacked', authorities: [Authority.USER] },
    },
    {
      path: '/about',
      name: 'about',
      component: About,
      meta: { layout: 'LayoutStacked' },
    },
    {
      path: '/login',
      name: 'login',
      component: Login,
      meta: { layout: 'SimpleLayout', isPublic: true },
    },
    {
      path: '/forbidden',
      name: 'forbidden',
      component: Error,
      meta: { error403: true, layout: 'SimpleLayout', isPublic: true },
    },
    {
      path: '/not-found',
      name: 'notFound',
      component: Error,
      meta: { error404: true, layout: 'SimpleLayout', isPublic: true },
    },
  ],
});

router.beforeEach(async (to, from, next) => {
  if (!to.matched.length) {
    next({ path: '/not-found' });
    return;
  }

  const publicPages = ['/login', '/register', '/logout'];
  const authRequired = !publicPages.includes(to.path);
  const authStore = useAuthStore();

  if (authRequired && !authStore.token) {
    authStore.returnUrl = to.fullPath;
    next('/login'); // Redirect to login page
  } else {
    next(); // Proceed to the next navigation step
  }
});
export default router;
