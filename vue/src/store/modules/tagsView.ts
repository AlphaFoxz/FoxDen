import {type RouteLocationNormalized} from 'vue-router';
import {defineStore} from 'pinia';
import {ref} from 'vue';

export const useTagsViewStore = defineStore('tagsView', () => {
  const visitedViews = ref<RouteLocationNormalized[]>([]);
  const cachedViews = ref<string[]>([]);
  const iframeViews = ref<RouteLocationNormalized[]>([]);

  const getVisitedViews = (): RouteLocationNormalized[] => visitedViews.value as RouteLocationNormalized[];

  const getIframeViews = (): RouteLocationNormalized[] => iframeViews.value as RouteLocationNormalized[];

  const getCachedViews = (): string[] => cachedViews.value;

  const addView = (view: RouteLocationNormalized) => {
    addVisitedView(view);
    addCachedView(view);
  };

  const addIframeView = (view: RouteLocationNormalized): void => {
    if (iframeViews.value.some((v: RouteLocationNormalized) => v.path === view.path)) {
      return;
    }

    iframeViews.value.push({...view, title: view.meta?.title ?? 'no-name'});
  };

  const delIframeView = async (view: RouteLocationNormalized): Promise<RouteLocationNormalized[]> => new Promise(resolve => {
    iframeViews.value = iframeViews.value.filter((item: RouteLocationNormalized) => item.path !== view.path);
    resolve([...(iframeViews.value as RouteLocationNormalized[])]);
  });

  const addVisitedView = (view: RouteLocationNormalized): void => {
    if (visitedViews.value.some((v: RouteLocationNormalized) => v.path === view.path)) {
      return;
    }

    visitedViews.value.push({...view, title: view.meta?.title ?? 'no-name'});
  };

  const delView = async (view: RouteLocationNormalized): Promise<{
    visitedViews: RouteLocationNormalized[];
    cachedViews: string[];
  }> => new Promise(resolve => {
    void delVisitedView(view);
    if (!isDynamicRoute(view)) {
      void delCachedView(view);
    }

    resolve({
      visitedViews: [...(visitedViews.value as RouteLocationNormalized[])],
      cachedViews: [...cachedViews.value],
    });
  });

  const delVisitedView = async (view: RouteLocationNormalized): Promise<RouteLocationNormalized[]> => new Promise(resolve => {
    for (const [i, v] of visitedViews.value.entries()) {
      if (v.path === view.path) {
        visitedViews.value.splice(i, 1);
        break;
      }
    }

    resolve([...(visitedViews.value as RouteLocationNormalized[])]);
  });

  const delCachedView = async (view?: RouteLocationNormalized): Promise<string[]> => {
    let viewName = '';
    if (view) {
      viewName = view.name as string;
    }

    return new Promise(resolve => {
      const index = cachedViews.value.indexOf(viewName);
      if (index !== -1) {
        cachedViews.value.splice(index, 1);
      }

      resolve([...cachedViews.value]);
    });
  };

  const delOthersViews = async (view: RouteLocationNormalized): Promise<{
    visitedViews: RouteLocationNormalized[];
    cachedViews: string[];
  }> => new Promise(resolve => {
    void delOthersVisitedViews(view);
    void delOthersCachedViews(view);
    resolve({
      visitedViews: [...(visitedViews.value as RouteLocationNormalized[])],
      cachedViews: [...cachedViews.value],
    });
  });

  const delOthersVisitedViews = async (view: RouteLocationNormalized): Promise<RouteLocationNormalized[]> => new Promise(resolve => {
    visitedViews.value = visitedViews.value.filter((v: RouteLocationNormalized) => v.meta?.affix ?? v.path === view.path);
    resolve([...(visitedViews.value as RouteLocationNormalized[])]);
  });

  const delOthersCachedViews = async (view: RouteLocationNormalized): Promise<string[]> => {
    const viewName = view.name as string;
    return new Promise(resolve => {
      const index = cachedViews.value.indexOf(viewName);
      cachedViews.value = index === -1 ? [] : cachedViews.value.slice(index, index + 1);

      resolve([...cachedViews.value]);
    });
  };

  const delAllViews = async (): Promise<{visitedViews: RouteLocationNormalized[]; cachedViews: string[]}> => new Promise(resolve => {
    void delAllVisitedViews();
    void delAllCachedViews();
    resolve({
      visitedViews: [...(visitedViews.value as RouteLocationNormalized[])],
      cachedViews: [...cachedViews.value],
    });
  });

  const delAllVisitedViews = async (): Promise<RouteLocationNormalized[]> => new Promise(resolve => {
    visitedViews.value = visitedViews.value.filter((tag: RouteLocationNormalized) => tag.meta?.affix);
    resolve([...(visitedViews.value as RouteLocationNormalized[])]);
  });

  const delAllCachedViews = async (): Promise<string[]> => new Promise(resolve => {
    cachedViews.value = [];
    resolve([...cachedViews.value]);
  });

  const updateVisitedView = (view: RouteLocationNormalized): void => {
    for (let v of visitedViews.value) {
      if (v.path === view.path) {
        v = Object.assign(v, view);
        break;
      }
    }
  };

  const delRightTags = async (view: RouteLocationNormalized): Promise<RouteLocationNormalized[]> => new Promise(resolve => {
    const index = visitedViews.value.findIndex((v: RouteLocationNormalized) => v.path === view.path);
    if (index === -1) {
      return;
    }

    visitedViews.value = visitedViews.value.filter((item: RouteLocationNormalized, idx: number) => {
      if (idx <= index || (item.meta?.affix)) {
        return true;
      }

      const i = cachedViews.value.indexOf(item.name as string);
      if (i !== -1) {
        cachedViews.value.splice(i, 1);
      }

      return false;
    });
    resolve([...(visitedViews.value as RouteLocationNormalized[])]);
  });

  const delLeftTags = async (view: RouteLocationNormalized): Promise<RouteLocationNormalized[]> => new Promise(resolve => {
    const index = visitedViews.value.findIndex((v: RouteLocationNormalized) => v.path === view.path);
    if (index === -1) {
      return;
    }

    visitedViews.value = visitedViews.value.filter((item: RouteLocationNormalized, idx: number) => {
      if (idx >= index || (item.meta?.affix)) {
        return true;
      }

      const i = cachedViews.value.indexOf(item.name as string);
      if (i !== -1) {
        cachedViews.value.splice(i, 1);
      }

      return false;
    });
    resolve([...(visitedViews.value as RouteLocationNormalized[])]);
  });

  const addCachedView = (view: RouteLocationNormalized): void => {
    const viewName = view.name as string;
    if (!viewName) {
      return;
    }

    if (cachedViews.value.includes(viewName)) {
      return;
    }

    if (!view.meta?.noCache) {
      cachedViews.value.push(viewName);
    }
  };

  const isDynamicRoute = (view: RouteLocationNormalized): boolean =>
    // 检查匹配的路由记录中是否有动态段
    view.matched.some(m => m.path.includes(':'));
  return {
    visitedViews,
    cachedViews,
    iframeViews,

    getVisitedViews,
    getIframeViews,
    getCachedViews,

    addVisitedView,
    addCachedView,
    delVisitedView,
    delCachedView,
    updateVisitedView,
    addView,
    delView,
    delAllViews,
    delAllVisitedViews,
    delAllCachedViews,
    delOthersViews,
    delRightTags,
    delLeftTags,
    addIframeView,
    delIframeView,
  };
});
