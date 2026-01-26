import PrimeVue from 'primevue/config';
import Aura from '@primeuix/themes/aura';
import {createApp, type Component} from 'vue';
import './assets/main.css';
import App from './App.vue';
import router from './router';

const app = createApp(App as Component);

app.use(router);
app.use(PrimeVue, {
  theme: {
    preset: Aura,
  },
});

app.mount('#app');
