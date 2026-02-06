import {type AxiosPromise} from 'axios';
import {
  type LoginData, type LoginResult, type VerifyCodeResult, type TenantInfo,
} from './types';
import request from '@/utils/request';
import {type UserInfo} from '@/api/system/user/types';

// Pc端固定客户端授权id
const clientId = import.meta.env.VITE_APP_CLIENT_ID;

/**
 * @param data {LoginData}
 * @returns
 */
export async function login(data: LoginData): AxiosPromise<LoginResult> {
  const parameters = {
    ...data,
    clientId: data.clientId || clientId,
    grantType: data.grantType || 'password',
  };
  return request({
    url: '/auth/login',
    headers: {
      isToken: false,
      isEncrypt: true,
      repeatSubmit: false,
    },
    method: 'post',
    data: parameters,
  });
}

// 注册方法
export async function register(data: any) {
  const parameters = {
    ...data,
    clientId,
    grantType: 'password',
  };
  return request({
    url: '/auth/register',
    headers: {
      isToken: false,
      isEncrypt: true,
      repeatSubmit: false,
    },
    method: 'post',
    data: parameters,
  });
}

/**
 * 注销
 */
export async function logout() {
  if (import.meta.env.VITE_APP_SSE === 'true') {
    request({
      url: '/resource/sse/close',
      method: 'get',
    });
  }

  return request({
    url: '/auth/logout',
    method: 'post',
  });
}

/**
 * 获取验证码
 */
export async function getCodeImg(): AxiosPromise<VerifyCodeResult> {
  return request({
    url: '/auth/code',
    headers: {
      isToken: false,
    },
    method: 'get',
    timeout: 20_000,
  });
}

/**
 * 第三方登录
 */
export async function callback(data: LoginData): AxiosPromise {
  const LoginData = {
    ...data,
    clientId,
    grantType: 'social',
  };
  return request({
    url: '/auth/social/callback',
    method: 'post',
    data: LoginData,
  });
}

// 获取用户详细信息
export async function getInfo(): AxiosPromise<UserInfo> {
  return request({
    url: '/system/user/getInfo',
    method: 'get',
  });
}

// 获取租户列表
export async function getTenantList(isToken: boolean): AxiosPromise<TenantInfo> {
  return request({
    url: '/auth/tenant/list',
    headers: {
      isToken,
    },
    method: 'get',
  });
}
