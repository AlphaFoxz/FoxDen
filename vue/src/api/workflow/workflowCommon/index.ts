import {type RouterJumpVo} from '@/api/workflow/workflowCommon/types';

const expose = {
  routerJump(routerJumpVo: RouterJumpVo, proxy: globalThis.ComponentPublicInstance) {
    proxy.$tab.closePage(proxy.$route);
    proxy.$router.push({
      path: routerJumpVo.formPath,
      query: {
        id: routerJumpVo.businessId,
        type: routerJumpVo.type,
        taskId: routerJumpVo.taskId,
      },
    });
  },
};

export default expose;
