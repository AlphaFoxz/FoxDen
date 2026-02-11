import {type AxiosPromise} from 'axios';
import {
  type DeptForm, type DeptQuery, type DeptVO,
} from './types';
import request from '@/utils/request';

// 查询部门列表
export const listDept = async (query?: DeptQuery) => request({
  url: '/system/dept/list',
  method: 'get',
  params: query,
});

/**
 * 通过deptIds查询部门
 * @param deptIds
 */
export const optionSelect = async (deptIds: Array<number | string>): AxiosPromise<DeptVO[]> => request({
  url: '/system/dept/optionselect?deptIds=' + deptIds.toString(),
  method: 'get',
});

// 查询部门列表（排除节点）
export const listDeptExcludeChild = async (deptId: string | number): AxiosPromise<DeptVO[]> => request({
  url: '/system/dept/list/exclude/' + deptId,
  method: 'get',
});

// 查询部门详细
export const getDept = async (deptId: string | number): AxiosPromise<DeptVO> => request({
  url: '/system/dept/' + deptId,
  method: 'get',
});

// 新增部门
export const addDept = async (data: DeptForm) => request({
  url: '/system/dept',
  method: 'post',
  data,
});

// 修改部门
export const updateDept = async (data: DeptForm) => request({
  url: '/system/dept',
  method: 'put',
  data,
});

// 删除部门
export const delDept = async (deptId: number | string) => request({
  url: '/system/dept/' + deptId,
  method: 'delete',
});
