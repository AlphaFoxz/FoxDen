import {type AxiosPromise} from 'axios';
import {
  type MenuQuery, type MenuVO, type MenuForm, type MenuTreeOption, type RoleMenuTree,
} from './types';
import request from '@/utils/request';

// 查询菜单列表
export const listMenu = async (query?: MenuQuery): AxiosPromise<MenuVO[]> => request({
  url: '/system/menu/list',
  method: 'get',
  params: query,
});

// 查询菜单详细
export const getMenu = async (menuId: string | number): AxiosPromise<MenuVO> => request({
  url: '/system/menu/' + menuId,
  method: 'get',
});

// 查询菜单下拉树结构
export const treeselect = async (): AxiosPromise<MenuTreeOption[]> => request({
  url: '/system/menu/treeselect',
  method: 'get',
});

// 根据角色ID查询菜单下拉树结构
export const roleMenuTreeselect = async (roleId: string | number): AxiosPromise<RoleMenuTree> => request({
  url: '/system/menu/roleMenuTreeselect/' + roleId,
  method: 'get',
});

// 根据角色ID查询菜单下拉树结构
export const tenantPackageMenuTreeselect = async (packageId: string | number): AxiosPromise<RoleMenuTree> => request({
  url: '/system/menu/tenantPackageMenuTreeselect/' + packageId,
  method: 'get',
});

// 新增菜单
export const addMenu = async (data: MenuForm) => request({
  url: '/system/menu',
  method: 'post',
  data,
});

// 修改菜单
export const updateMenu = async (data: MenuForm) => request({
  url: '/system/menu',
  method: 'put',
  data,
});

// 删除菜单
export const delMenu = async (menuId: string | number) => request({
  url: '/system/menu/' + menuId,
  method: 'delete',
});

// 级联删除菜单
export const cascadeDelMenu = async (menuIds: Array<string | number>) => request({
  url: '/system/menu/cascade/' + menuIds.toString(),
  method: 'delete',
});
