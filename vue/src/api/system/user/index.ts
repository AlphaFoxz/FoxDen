import {type AxiosPromise} from 'axios';
import {type DeptTreeVO} from '../dept/types';
import {
  type UserForm, type UserQuery, type UserVO, type UserInfoVO,
} from './types';
import {type RoleVO} from '@/api/system/role/types';
import request from '@/utils/request';
import {parseStringEmpty} from '@/utils/ruoyi';

/**
 * 查询用户列表
 * @param query
 */
export const listUser = async (query: UserQuery): AxiosPromise<UserVO[]> => request({
  url: '/system/user/list',
  method: 'get',
  params: query,
});

/**
 * 通过用户ids查询用户
 * @param userIds
 */
export const optionSelect = async (userIds: Array<number | string>): AxiosPromise<UserVO[]> => request({
  url: '/system/user/optionselect?userIds=' + userIds.toString(),
  method: 'get',
});

/**
 * 获取用户详情
 * @param userId
 */
export const getUser = async (userId?: string | number): AxiosPromise<UserInfoVO> => request({
  url: '/system/user/' + parseStringEmpty(userId),
  method: 'get',
});

/**
 * 新增用户
 */
export const addUser = async (data: UserForm) => request({
  url: '/system/user',
  method: 'post',
  data,
});

/**
 * 修改用户
 */
export const updateUser = async (data: UserForm) => request({
  url: '/system/user',
  method: 'put',
  data,
});

/**
 * 删除用户
 * @param userId 用户ID
 */
export const delUser = async (userId: Array<string | number> | string | number) => request({
  url: '/system/user/' + userId.toString(),
  method: 'delete',
});

/**
 * 用户密码重置
 * @param userId 用户ID
 * @param password 密码
 */
export const resetUserPwd = async (userId: string | number, password: string) => {
  const data = {
    userId,
    password,
  };
  return request({
    url: '/system/user/resetPwd',
    method: 'put',
    headers: {
      isEncrypt: true,
      repeatSubmit: false,
    },
    data,
  });
};

/**
 * 用户状态修改
 * @param userId 用户ID
 * @param status 用户状态
 */
export const changeUserStatus = async (userId: number | string, status: string) => {
  const data = {
    userId,
    status,
  };
  return request({
    url: '/system/user/changeStatus',
    method: 'put',
    data,
  });
};

/**
 * 查询用户个人信息
 */
export const getUserProfile = async (): AxiosPromise<UserInfoVO> => request({
  url: '/system/user/profile',
  method: 'get',
});

/**
 * 修改用户个人信息
 * @param data 用户信息
 */
export const updateUserProfile = async (data: UserForm) => request({
  url: '/system/user/profile',
  method: 'put',
  data,
});

/**
 * 用户密码重置
 * @param oldPassword 旧密码
 * @param newPassword 新密码
 */
export const updateUserPwd = async (oldPassword: string, newPassword: string) => {
  const data = {
    oldPassword,
    newPassword,
  };
  return request({
    url: '/system/user/profile/updatePwd',
    method: 'put',
    headers: {
      isEncrypt: true,
      repeatSubmit: false,
    },
    data,
  });
};

/**
 * 用户头像上传
 * @param data 头像文件
 */
export const uploadAvatar = async (data: FormData) => request({
  url: '/system/user/profile/avatar',
  method: 'post',
  data,
});

/**
 * 查询授权角色
 * @param userId 用户ID
 */
export const getAuthRole = async (userId: string | number): AxiosPromise<{user: UserVO; roles: RoleVO[]}> => request({
  url: '/system/user/authRole/' + userId,
  method: 'get',
});

/**
 * 保存授权角色
 * @param data 用户ID
 */
export const updateAuthRole = async (data: {userId: string; roleIds: string}) => request({
  url: '/system/user/authRole',
  method: 'put',
  params: data,
});

/**
 * 查询当前部门的所有用户信息
 * @param deptId
 */
export const listUserByDeptId = async (deptId: string | number): AxiosPromise<UserVO[]> => request({
  url: '/system/user/list/dept/' + deptId,
  method: 'get',
});

/**
 * 查询部门下拉树结构
 */
export const deptTreeSelect = async (): AxiosPromise<DeptTreeVO[]> => request({
  url: '/system/user/deptTree',
  method: 'get',
});

const expose = {
  listUser,
  getUser,
  optionSelect,
  addUser,
  updateUser,
  delUser,
  resetUserPwd,
  changeUserStatus,
  getUserProfile,
  updateUserProfile,
  updateUserPwd,
  uploadAvatar,
  getAuthRole,
  updateAuthRole,
  deptTreeSelect,
  listUserByDeptId,
};

export default expose;
