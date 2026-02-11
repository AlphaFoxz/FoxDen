import {type AxiosPromise} from 'axios';
import {type OperLogQuery, type OperLogVO} from './types';
import request from '@/utils/request';

// 查询操作日志列表
export async function list(query: OperLogQuery): AxiosPromise<OperLogVO[]> {
  return request({
    url: '/monitor/operlog/list',
    method: 'get',
    params: query,
  });
}

// 删除操作日志
export async function delOperlog(operId: string | number | Array<string | number>) {
  return request({
    url: '/monitor/operlog/' + operId.toString(),
    method: 'delete',
  });
}

// 清空操作日志
export async function cleanOperlog() {
  return request({
    url: '/monitor/operlog/clean',
    method: 'delete',
  });
}
