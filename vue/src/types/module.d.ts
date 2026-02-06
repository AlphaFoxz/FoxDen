import type modal from '@/plugins/modal';
import type tab from '@/plugins/tab';
import type download from '@/plugins/download';
import type auth from '@/plugins/auth';
import type cache from '@/plugins/cache';
import type animate from '@/animate';
import {type useDict} from '@/utils/dict';
import {
  type handleTree, type addDateRange, type selectDictLabel, type selectDictLabels, type parseTime,
} from '@/utils/ruoyi';
import {type getConfigKey, type updateConfigByKey} from '@/api/system/config';
import {type download as rd} from '@/utils/request';
import type {LanguageType} from '@/lang';

export {};

declare module 'vue' {
  interface ComponentCustomProperties {
    // 全局方法声明
    $modal: typeof modal;
    $tab: typeof tab;
    $download: typeof download;
    $auth: typeof auth;
    $cache: typeof cache;
    animate: typeof animate;
    /**
     * I18n $t方法支持ts类型提示
     * @param key i18n key
     */
    $t(key: ObjKeysToUnion<LanguageType>): string;

    useDict: typeof useDict;
    addDateRange: typeof addDateRange;
    download: typeof rd;
    handleTree: typeof handleTree;
    getConfigKey: typeof getConfigKey;
    updateConfigByKey: typeof updateConfigByKey;
    selectDictLabel: typeof selectDictLabel;
    selectDictLabels: typeof selectDictLabels;
    parseTime: typeof parseTime;
  }
}

/**
 * { a: 1, b: { ba: { baa: 1, bab: 2 }, bb: 2} } ---> a | b.ba.baa | b.ba.bab | b.bb
 * https://juejin.cn/post/7280062870670606397
 */
export type ObjKeysToUnion<T, P extends string = ''> = T extends Record<string, unknown>
  ? {
    [K in keyof T]: ObjKeysToUnion<T[K], P extends '' ? `${K & string}` : `${P}.${K & string}`>;
  }[keyof T]
  : P;
