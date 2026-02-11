import request from '@/utils/request';

// 获取跳转URL
export async function authRouterUrl(source: string, tenantId: string) {
  return request({
    url: '/auth/binding/' + source,
    method: 'get',
    params: {
      tenantId,
      domain: globalThis.location.host,
    },
  });
}

// 解绑账号
export async function authUnlock(authId: string) {
  return request({
    url: '/auth/unlock/' + authId,
    method: 'delete',
  });
}

// 获取授权列表
export async function getAuthList() {
  return request({
    url: '/system/social/list',
    method: 'get',
  });
}
