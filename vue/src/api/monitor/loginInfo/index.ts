import {type AxiosPromise} from 'axios';
import {type LoginInfoQuery, type LoginInfoVO} from './types';
import request from '@/utils/request';

// 查询登录日志列表
export async function list(query: LoginInfoQuery): AxiosPromise<LoginInfoVO[]> {
  return request({
    url: '/monitor/logininfor/list',
    method: 'get',
    params: query,
  });
}

// 删除登录日志
export async function delLoginInfo(infoId: string | number | Array<string | number>) {
  return request({
    url: '/monitor/logininfor/' + infoId.toString(),
    method: 'delete',
  });
}

// 解锁用户登录状态
export async function unlockLoginInfo(userName: string | string[]) {
  return request({
    url: '/monitor/logininfor/unlock/' + userName.toString(),
    method: 'get',
  });
}

// 清空登录日志
export async function cleanLoginInfo() {
  return request({
    url: '/monitor/logininfor/clean',
    method: 'delete',
  });
}
