import {type AxiosPromise} from 'axios';
import request from '@/utils/request';
import {type ClientVO, type ClientForm, type ClientQuery} from '@/api/system/client/types';

/**
 * 查询客户端管理列表
 * @param query
 * @returns {*}
 */

export const listClient = async (query?: ClientQuery): AxiosPromise<ClientVO[]> => request({
  url: '/system/client/list',
  method: 'get',
  params: query,
});

/**
 * 查询客户端管理详细
 * @param id
 */
export const getClient = async (id: string | number): AxiosPromise<ClientVO> => request({
  url: '/system/client/' + id,
  method: 'get',
});

/**
 * 新增客户端管理
 * @param data
 */
export const addClient = async (data: ClientForm) => request({
  url: '/system/client',
  method: 'post',
  data,
});

/**
 * 修改客户端管理
 * @param data
 */
export const updateClient = async (data: ClientForm) => request({
  url: '/system/client',
  method: 'put',
  data,
});

/**
 * 删除客户端管理
 * @param id
 */
export const delClient = async (id: string | number | Array<string | number>) => request({
  url: '/system/client/' + id.toString(),
  method: 'delete',
});

/**
 * 状态修改
 * @param clientId 客户端id
 * @param status 状态
 */
export async function changeStatus(clientId: string, status: string) {
  const data = {
    clientId,
    status,
  };
  return request({
    url: '/system/client/changeStatus',
    method: 'put',
    data,
  });
}
