import {type AxiosPromise} from 'axios';
import {type DictTypeForm, type DictTypeVO, type DictTypeQuery} from './types';
import request from '@/utils/request';

// 查询字典类型列表
export async function listType(query: DictTypeQuery): AxiosPromise<DictTypeVO[]> {
  return request({
    url: '/system/dict/type/list',
    method: 'get',
    params: query,
  });
}

// 查询字典类型详细
export async function getType(dictId: number | string): AxiosPromise<DictTypeVO> {
  return request({
    url: '/system/dict/type/' + dictId,
    method: 'get',
  });
}

// 新增字典类型
export async function addType(data: DictTypeForm) {
  return request({
    url: '/system/dict/type',
    method: 'post',
    data,
  });
}

// 修改字典类型
export async function updateType(data: DictTypeForm) {
  return request({
    url: '/system/dict/type',
    method: 'put',
    data,
  });
}

// 删除字典类型
export async function delType(dictId: string | number | Array<string | number>) {
  return request({
    url: '/system/dict/type/' + dictId.toString(),
    method: 'delete',
  });
}

// 刷新字典缓存
export async function refreshCache() {
  return request({
    url: '/system/dict/type/refreshCache',
    method: 'delete',
  });
}

// 获取字典选择框列表
export async function optionselect(): AxiosPromise<DictTypeVO[]> {
  return request({
    url: '/system/dict/type/optionselect',
    method: 'get',
  });
}
