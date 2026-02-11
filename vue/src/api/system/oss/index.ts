import {type AxiosPromise} from 'axios';
import {type OssQuery, type OssVO} from './types';
import request from '@/utils/request';

// 查询OSS对象存储列表
export async function listOss(query: OssQuery): AxiosPromise<OssVO[]> {
  return request({
    url: '/resource/oss/list',
    method: 'get',
    params: query,
  });
}

// 查询OSS对象基于id串
export async function listByIds(ossId: string | number): AxiosPromise<OssVO[]> {
  return request({
    url: '/resource/oss/listByIds/' + ossId,
    method: 'get',
  });
}

// 删除OSS对象存储
export async function delOss(ossId: string | number | Array<string | number>) {
  return request({
    url: '/resource/oss/' + ossId.toString(),
    method: 'delete',
  });
}
