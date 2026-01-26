import {ref} from 'vue';
import {createSingletonAgg} from 'vue-fn/domain';
import {createI18n} from 'vue-i18n';
import {Language} from './types';
import zh_CN from '@/domain/language/zh-cn';
import en_US from '@/domain/language/en-us';

const agg = createSingletonAgg(() => {
  const currentLanguage = ref<Language>(Language.zhCN);
  return {
    states: {
      currentLanguage,
    },
    commands: {},
  };
});

export function useLanguageAgg() {
  return agg.api;
}

const i18n = createI18n({
  globalInjection: true,
  allowComposition: true,
  legacy: false,
  locale: agg.api.states.currentLanguage.value,
  messages: {
    zh_CN,
    en_US,
  },
});
export default i18n;
