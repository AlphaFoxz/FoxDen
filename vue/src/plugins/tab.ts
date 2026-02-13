import {type RouteLocationMatched, type RouteLocationNormalized, type RouteLocationRaw} from 'vue-router';
import router from '@/router';
import {useTagsViewStore} from '@/store/modules/tagsView';

const expose = {
  /**
   * 刷新当前tab页签
   * @param obj 标签对象
   */
  async refreshPage(object?: RouteLocationNormalized): Promise<void> {
    const {path, query, matched} = router.currentRoute.value;
    if (object === undefined) {
      for (const m of matched) {
        if (m.components && m.components.default && m.components.default.name && !['Layout', 'ParentView'].includes(m.components.default.name)) {
          object = {
            name: m.components.default.name,
            path,
            query,
            matched: undefined,
            fullPath: undefined,
            hash: undefined,
            params: undefined,
            redirectedFrom: undefined,
            meta: undefined,
          };
        }
      }
    }

    let query1: undefined | Record<string, unknown> = {};
    let path1: undefined | string = '';
    if (object) {
      query1 = object.query;
      path1 = object.path;
    }

    await useTagsViewStore().delCachedView(object);
    await router.replace({
      path: '/redirect' + path1,
      query: query1,
    });
  },
  // 关闭当前tab页签，打开新页签
  closeOpenPage(object: RouteLocationRaw): void {
    void useTagsViewStore().delView(router.currentRoute.value);
    if (object !== undefined) {
      void router.push(object);
    }
  },
  // 关闭指定tab页签
  async closePage(object?: RouteLocationNormalized): Promise<{visitedViews: RouteLocationNormalized[]; cachedViews: string[]} | any> {
    if (object === undefined) {
      // prettier-ignore
      const {visitedViews} = await useTagsViewStore().delView(router.currentRoute.value);
      const latestView = visitedViews.at(-1);
      if (latestView) {
        return router.push(latestView.fullPath);
      }

      return router.push('/');
    }

    return useTagsViewStore().delView(object);
  },
  // 关闭所有tab页签
  async closeAllPage() {
    return useTagsViewStore().delAllViews();
  },
  // 关闭左侧tab页签
  async closeLeftPage(object?: RouteLocationNormalized) {
    return useTagsViewStore().delLeftTags(object ?? router.currentRoute.value);
  },
  // 关闭右侧tab页签
  async closeRightPage(object?: RouteLocationNormalized) {
    return useTagsViewStore().delRightTags(object ?? router.currentRoute.value);
  },
  // 关闭其他tab页签
  async closeOtherPage(object?: RouteLocationNormalized) {
    return useTagsViewStore().delOthersViews(object ?? router.currentRoute.value);
  },
  /**
   * 打开tab页签
   * @param url 路由地址
   * @param title 标题
   * @param query 参数
   */
  async openPage(url: string, title?: string, query?: Record<string, unknown>) {
    const object = {path: url, query: {...query, title}};
    return router.push(object);
  },
  /**
   * 修改tab页签
   * @param obj 标签对象
   */
  updatePage(object: RouteLocationNormalized) {
    useTagsViewStore().updateVisitedView(object);
  },
};
export default expose;
