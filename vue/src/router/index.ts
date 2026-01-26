import {createRouter, createWebHistory} from 'vue-router';
import {type Component} from 'vue';
import Home from '@/views/home.vue';
import About from '@/views/about.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home as Component,
    },
    {
      path: '/about',
      name: 'about',
      // Route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: About as Component,
    },
  ],
});

export default router;
