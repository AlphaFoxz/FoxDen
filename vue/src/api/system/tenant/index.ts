import {type AxiosPromise} from 'axios';
import {type TenantForm, type TenantQuery, type TenantVO} from './types';
import request from '@/utils/request';

// 查询租户列表
export async function listTenant(query: TenantQuery): AxiosPromise<TenantVO[]> {
  return request({
    url: '/system/tenant/list',
    method: 'get',
    params: query,
  });
}

// 查询租户详细
export async function getTenant(id: string | number): AxiosPromise<TenantVO> {
  return request({
    url: '/system/tenant/' + id,
    method: 'get',
  });
}

// 新增租户
export async function addTenant(data: TenantForm) {
  return request({
    url: '/system/tenant',
    method: 'post',
    headers: {
      isEncrypt: true,
      repeatSubmit: false,
    },
    data,
  });
}

// 修改租户
export async function updateTenant(data: TenantForm) {
  return request({
    url: '/system/tenant',
    method: 'put',
    data,
  });
}

// 租户状态修改
export async function changeTenantStatus(id: string | number, tenantId: string | number, status: string) {
  const data = {
    id,
    tenantId,
    status,
  };
  return request({
    url: '/system/tenant/changeStatus',
    method: 'put',
    data,
  });
}

// 删除租户
export async function delTenant(id: string | number | Array<string | number>) {
  return request({
    url: '/system/tenant/' + id.toString(),
    method: 'delete',
  });
}

// 动态切换租户
export async function dynamicTenant(tenantId: string | number) {
  return request({
    url: '/system/tenant/dynamic/' + tenantId,
    method: 'get',
  });
}

// 清除动态租户
export async function dynamicClear() {
  return request({
    url: '/system/tenant/dynamic/clear',
    method: 'get',
  });
}

// 同步租户套餐
export async function syncTenantPackage(tenantId: string | number, packageId: string | number) {
  const data = {
    tenantId,
    packageId,
  };
  return request({
    url: '/system/tenant/syncTenantPackage',
    method: 'get',
    params: data,
  });
}

// 同步租户字典
export async function syncTenantDict() {
  return request({
    url: '/system/tenant/syncTenantDict',
    method: 'get',
  });
}

// 同步租户字典
export async function syncTenantConfig() {
  return request({
    url: '/system/tenant/syncTenantConfig',
    method: 'get',
  });
}
