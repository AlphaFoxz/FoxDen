import {type AxiosPromise} from 'axios';
import {type OnlineQuery, type OnlineVO} from './types';
import request from '@/utils/request';

// 查询在线用户列表
export async function list(query: OnlineQuery): AxiosPromise<OnlineVO[]> {
  return request({
    url: '/monitor/online/list',
    method: 'get',
    params: query,
  });
}

// 强退用户
export async function forceLogout(tokenId: string) {
  return request({
    url: '/monitor/online/' + tokenId,
    method: 'delete',
  });
}

// 获取当前用户登录在线设备
export async function getOnline() {
  return request({
    url: '/monitor/online',
    method: 'get',
  });
}

// 删除当前在线设备
export async function delOnline(tokenId: string) {
  return request({
    url: '/monitor/online/myself/' + tokenId,
    method: 'delete',
  });
}
