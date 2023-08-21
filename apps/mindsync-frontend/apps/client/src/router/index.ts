import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores';
import HomeView from '../views/HomeView.vue';

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
      meta: { layout: 'LayoutStacked' },
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
      meta: { layout: 'SimpleLayout' },
    },
    {
      path: '/forbidden',
      name: 'Forbidden',
      component: Error,
      meta: { error403: true, layout: 'SimpleLayout' },
    },
    {
      path: '/not-found',
      name: 'NotFound',
      component: Error,
      meta: { error404: true, layout: 'SimpleLayout' },
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
