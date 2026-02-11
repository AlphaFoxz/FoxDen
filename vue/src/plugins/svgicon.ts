import * as ElementPlusIconsVue from '@element-plus/icons-vue';
import {type App} from 'vue';

const expose = {
  install(app: App) {
    for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
      app.component(key, component);
    }
  },
};
export default expose;
