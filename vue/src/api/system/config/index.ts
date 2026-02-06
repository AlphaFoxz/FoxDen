import {type AxiosPromise} from 'axios';
import {type ConfigForm, type ConfigQuery, type ConfigVO} from './types';
import request from '@/utils/request';

// 查询参数列表
export async function listConfig(query: ConfigQuery): AxiosPromise<ConfigVO[]> {
  return request({
    url: '/system/config/list',
    method: 'get',
    params: query,
  });
}

// 查询参数详细
export async function getConfig(configId: string | number): AxiosPromise<ConfigVO> {
  return request({
    url: '/system/config/' + configId,
    method: 'get',
  });
}

// 根据参数键名查询参数值
export async function getConfigKey(configKey: string): AxiosPromise<string> {
  return request({
    url: '/system/config/configKey/' + configKey,
    method: 'get',
  });
}

// 新增参数配置
export async function addConfig(data: ConfigForm) {
  return request({
    url: '/system/config',
    method: 'post',
    data,
  });
}

// 修改参数配置
export async function updateConfig(data: ConfigForm) {
  return request({
    url: '/system/config',
    method: 'put',
    data,
  });
}

// 修改参数配置
export async function updateConfigByKey(key: string, value: any) {
  return request({
    url: '/system/config/updateByKey',
    method: 'put',
    data: {
      configKey: key,
      configValue: value,
    },
  });
}

// 删除参数配置
export async function delConfig(configId: string | number | Array<string | number>) {
  return request({
    url: '/system/config/' + configId,
    method: 'delete',
  });
}

// 刷新参数缓存
export async function refreshCache() {
  return request({
    url: '/system/config/refreshCache',
    method: 'delete',
  });
}
