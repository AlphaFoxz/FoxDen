import zhCN from 'element-plus/es/locale/lang/zh-cn';
import enUS from 'element-plus/es/locale/lang/en';
import {defineStore} from 'pinia';
import {useStorage} from '@vueuse/core';
import {ref, reactive, computed} from 'vue';

export const useAppStore = defineStore('app', () => {
  const sidebarStatus = useStorage('sidebarStatus', '1');
  const sidebar = reactive({
    opened: sidebarStatus.value ? Boolean(+sidebarStatus.value) : true,
    withoutAnimation: false,
    hide: false,
  });
  const device = ref<string>('desktop');
  const size = useStorage<'large' | 'default' | 'small'>('size', 'default');

  // 语言
  const language = useStorage('language', 'zh_CN');
  const languageObject: any = {
    en_US: enUS,
    zh_CN: zhCN,
  };
  const locale = computed(() => languageObject[language.value]);

  const toggleSideBar = (withoutAnimation: boolean) => {
    if (sidebar.hide) {
      return false;
    }

    sidebar.opened = !sidebar.opened;
    sidebar.withoutAnimation = withoutAnimation;
    sidebarStatus.value = sidebar.opened ? '1' : '0';
  };

  const closeSideBar = ({withoutAnimation}: any): void => {
    sidebarStatus.value = '0';
    sidebar.opened = false;
    sidebar.withoutAnimation = withoutAnimation;
  };

  const toggleDevice = (d: string): void => {
    device.value = d;
  };

  const setSize = (s: 'large' | 'default' | 'small'): void => {
    size.value = s;
  };

  const toggleSideBarHide = (status: boolean): void => {
    sidebar.hide = status;
  };

  const changeLanguage = (value: string): void => {
    language.value = value;
  };

  return {
    device,
    sidebar,
    language,
    locale,
    size,
    changeLanguage,
    toggleSideBar,
    closeSideBar,
    toggleDevice,
    setSize,
    toggleSideBarHide,
  };
});
