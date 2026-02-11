import {type AxiosPromise} from 'axios';
import {type OssConfigForm, type OssConfigQuery, type OssConfigVO} from './types';
import request from '@/utils/request';

// 查询对象存储配置列表
export async function listOssConfig(query: OssConfigQuery): AxiosPromise<OssConfigVO[]> {
  return request({
    url: '/resource/oss/config/list',
    method: 'get',
    params: query,
  });
}

// 查询对象存储配置详细
export async function getOssConfig(ossConfigId: string | number): AxiosPromise<OssConfigVO> {
  return request({
    url: '/resource/oss/config/' + ossConfigId,
    method: 'get',
  });
}

// 新增对象存储配置
export async function addOssConfig(data: OssConfigForm) {
  return request({
    url: '/resource/oss/config',
    method: 'post',
    data,
  });
}

// 修改对象存储配置
export async function updateOssConfig(data: OssConfigForm) {
  return request({
    url: '/resource/oss/config',
    method: 'put',
    data,
  });
}

// 删除对象存储配置
export async function delOssConfig(ossConfigId: string | number | Array<string | number>) {
  return request({
    url: '/resource/oss/config/' + ossConfigId.toString(),
    method: 'delete',
  });
}

// 对象存储状态修改
export async function changeOssConfigStatus(ossConfigId: string | number, status: string, configKey: string) {
  const data = {
    ossConfigId,
    status,
    configKey,
  };
  return request({
    url: '/resource/oss/config/changeStatus',
    method: 'put',
    data,
  });
}
