import {type AxiosPromise} from 'axios';
import {type RouteRecordRaw} from 'vue-router';
import request from '@/utils/request';

// 获取路由
export async function getRouters(): AxiosPromise<RouteRecordRaw[]> {
  return request({
    url: '/system/menu/getRouters',
    method: 'get',
  });
}
