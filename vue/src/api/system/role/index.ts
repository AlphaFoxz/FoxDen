import {type AxiosPromise} from 'axios';
import {type RoleQuery, type RoleVO, type RoleDeptTree} from './types';
import {type UserVO, type UserQuery} from '@/api/system/user/types';
import request from '@/utils/request';

export const listRole = async (query: RoleQuery): AxiosPromise<RoleVO[]> => request({
  url: '/system/role/list',
  method: 'get',
  params: query,
});

/**
 * 通过roleIds查询角色
 * @param roleIds
 */
export const optionSelect = async (roleIds: Array<number | string>): AxiosPromise<RoleVO[]> => request({
  url: '/system/role/optionselect?roleIds=' + roleIds.toString(),
  method: 'get',
});

/**
 * 查询角色详细
 */
export const getRole = async (roleId: string | number): AxiosPromise<RoleVO> => request({
  url: '/system/role/' + roleId,
  method: 'get',
});

/**
 * 新增角色
 */
export const addRole = async (data: Record<string, unknown>) => request({
  url: '/system/role',
  method: 'post',
  data,
});

/**
 * 修改角色
 * @param data
 */
export const updateRole = async (data: Record<string, unknown>) => request({
  url: '/system/role',
  method: 'put',
  data,
});

/**
 * 角色数据权限
 */
export const dataScope = async (data: Record<string, unknown>) => request({
  url: '/system/role/dataScope',
  method: 'put',
  data,
});

/**
 * 角色状态修改
 */
export const changeRoleStatus = async (roleId: string | number, status: string) => {
  const data = {
    roleId,
    status,
  };
  return request({
    url: '/system/role/changeStatus',
    method: 'put',
    data,
  });
};

/**
 * 删除角色
 */
export const delRole = async (roleId: Array<string | number> | string | number) => request({
  url: '/system/role/' + roleId.toString(),
  method: 'delete',
});

/**
 * 查询角色已授权用户列表
 */
export const allocatedUserList = async (query: UserQuery): AxiosPromise<UserVO[]> => request({
  url: '/system/role/authUser/allocatedList',
  method: 'get',
  params: query,
});

/**
 * 查询角色未授权用户列表
 */
export const unallocatedUserList = async (query: UserQuery): AxiosPromise<UserVO[]> => request({
  url: '/system/role/authUser/unallocatedList',
  method: 'get',
  params: query,
});

/**
 * 取消用户授权角色
 */
export const authUserCancel = async (data: Record<string, unknown>) => request({
  url: '/system/role/authUser/cancel',
  method: 'put',
  data,
});

/**
 * 批量取消用户授权角色
 */
export const authUserCancelAll = async (data: Record<string, unknown>) => request({
  url: '/system/role/authUser/cancelAll',
  method: 'put',
  params: data,
});

/**
 * 授权用户选择
 */
export const authUserSelectAll = async (data: Record<string, unknown>) => request({
  url: '/system/role/authUser/selectAll',
  method: 'put',
  params: data,
});

// 根据角色ID查询部门树结构
export const deptTreeSelect = async (roleId: string | number): AxiosPromise<RoleDeptTree> => request({
  url: '/system/role/deptTree/' + roleId,
  method: 'get',
});

const expose = {
  optionSelect,
  listRole,
};

export default expose;
