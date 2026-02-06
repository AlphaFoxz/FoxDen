import {createApp} from 'vue';
// global css
import 'virtual:uno.css';
import 'element-plus/theme-chalk/dark/css-vars.css';

// App、router、store
import HighLight from '@highlightjs/vue-plugin';
import 'highlight.js/lib/common';
import 'highlight.js/styles/atom-one-dark.css';
import 'virtual:svg-icons-register';
import VXETable from 'vxe-table';
import 'vxe-table/lib/style.css';
import {ElDialog} from 'element-plus';
import App from './App.vue';
import store from './store';
import router from './router';

// 自定义指令
import directive from './directive';

// 注册插件
import plugins from './plugins/index'; // Plugins

// 高亮组件
// import 'highlight.js/styles/a11y-light.css';

// Svg图标
import ElementIcons from '@/plugins/svgicon';

// Permission control
import './permission';

// 开发者工具保护
import {initDevToolsProtection} from '@/utils/devtools-protection';

// 国际化
import i18n from '@/lang/index';

// VxeTable

// 修改 el-dialog 默认点击遮照为不关闭
import '@/assets/styles/index.scss';

VXETable.setConfig({
  zIndex: 999_999,
});

ElDialog.props.closeOnClickModal.default = false;

const app = createApp(App);

app.use(HighLight);
app.use(ElementIcons);
app.use(router);
app.use(store);
app.use(i18n);
app.use(VXETable);
app.use(plugins);
// 自定义指令
directive(app);

app.mount('#app');

// 初始化开发者工具保护（仅生产环境）
initDevToolsProtection();
