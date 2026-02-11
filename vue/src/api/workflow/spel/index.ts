import {type AxiosPromise} from 'axios';
import request from '@/utils/request';
import {type SpelVO, type SpelForm, type SpelQuery} from '@/api/workflow/spel/types';

/**
 * 查询流程spel表达式定义列表
 * @param query
 * @returns {*}
 */

export const listSpel = async (query?: SpelQuery): AxiosPromise<SpelVO[]> => request({
  url: '/workflow/spel/list',
  method: 'get',
  params: query,
});

/**
 * 查询流程spel表达式定义详细
 * @param id
 */
export const getSpel = async (id: string | number): AxiosPromise<SpelVO> => request({
  url: '/workflow/spel/' + id,
  method: 'get',
});

/**
 * 新增流程spel表达式定义
 * @param data
 */
export const addSpel = async (data: SpelForm) => request({
  url: '/workflow/spel',
  method: 'post',
  data,
});

/**
 * 修改流程spel表达式定义
 * @param data
 */
export const updateSpel = async (data: SpelForm) => request({
  url: '/workflow/spel',
  method: 'put',
  data,
});

/**
 * 删除流程spel表达式定义
 * @param id
 */
export const delSpel = async (id: string | number | Array<string | number>) => request({
  url: '/workflow/spel/' + id.toString(),
  method: 'delete',
});
