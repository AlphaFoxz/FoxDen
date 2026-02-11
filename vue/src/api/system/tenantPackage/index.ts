import {type AxiosPromise} from 'axios';
import {type TenantPkgForm, type TenantPkgQuery, type TenantPkgVO} from './types';
import request from '@/utils/request';

// 查询租户套餐列表
export async function listTenantPackage(query?: TenantPkgQuery): AxiosPromise<TenantPkgVO[]> {
  return request({
    url: '/system/tenant/package/list',
    method: 'get',
    params: query,
  });
}

// 查询租户套餐下拉选列表
export async function selectTenantPackage(): AxiosPromise<TenantPkgVO[]> {
  return request({
    url: '/system/tenant/package/selectList',
    method: 'get',
  });
}

// 查询租户套餐详细
export async function getTenantPackage(packageId: string | number): AxiosPromise<TenantPkgVO> {
  return request({
    url: '/system/tenant/package/' + packageId,
    method: 'get',
  });
}

// 新增租户套餐
export async function addTenantPackage(data: TenantPkgForm) {
  return request({
    url: '/system/tenant/package',
    method: 'post',
    data,
  });
}

// 修改租户套餐
export async function updateTenantPackage(data: TenantPkgForm) {
  return request({
    url: '/system/tenant/package',
    method: 'put',
    data,
  });
}

// 租户套餐状态修改
export async function changePackageStatus(packageId: number | string, status: string) {
  const data = {
    packageId,
    status,
  };
  return request({
    url: '/system/tenant/package/changeStatus',
    method: 'put',
    data,
  });
}

// 删除租户套餐
export async function delTenantPackage(packageId: string | number | Array<string | number>) {
  return request({
    url: '/system/tenant/package/' + packageId,
    method: 'delete',
  });
}
