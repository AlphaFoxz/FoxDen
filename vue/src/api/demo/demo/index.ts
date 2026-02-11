import {type AxiosPromise} from 'axios';
import request from '@/utils/request';
import {type DemoVO, type DemoForm, type DemoQuery} from '@/api/demo/demo/types';

/**
 * 查询测试单列表
 * @param query
 * @returns {*}
 */
export const listDemo = async (query?: DemoQuery): AxiosPromise<DemoVO[]> => request({
  url: '/demo/demo/list',
  method: 'get',
  params: query,
});

/**
 * 查询测试单详细
 * @param id
 */
export const getDemo = async (id: string | number): AxiosPromise<DemoVO> => request({
  url: '/demo/demo/' + id,
  method: 'get',
});

/**
 * 新增测试单
 * @param data
 */
export const addDemo = async (data: DemoForm) => request({
  url: '/demo/demo',
  method: 'post',
  data,
});

/**
 * 修改测试单
 * @param data
 */
export const updateDemo = async (data: DemoForm) => request({
  url: '/demo/demo',
  method: 'put',
  data,
});

/**
 * 删除测试单
 * @param id
 */
export const delDemo = async (id: string | number | Array<string | number>) => request({
  url: '/demo/demo/' + id.toString(),
  method: 'delete',
});
