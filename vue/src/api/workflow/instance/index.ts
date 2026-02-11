import {type AxiosPromise} from 'axios';
import request from '@/utils/request';
import {type FlowInstanceQuery, type FlowInstanceVO} from '@/api/workflow/instance/types';

/**
 * 查询运行中实例列表
 * @param query
 * @returns {*}
 */
export const pageByRunning = async (query: FlowInstanceQuery): AxiosPromise<FlowInstanceVO[]> => request({
  url: '/workflow/instance/pageByRunning',
  method: 'get',
  params: query,
});

/**
 * 查询已完成实例列表
 * @param query
 * @returns {*}
 */
export const pageByFinish = async (query: FlowInstanceQuery): AxiosPromise<FlowInstanceVO[]> => request({
  url: '/workflow/instance/pageByFinish',
  method: 'get',
  params: query,
});

/**
 * 通过业务id获取历史流程图
 */
export const flowHisTaskList = async (businessId: string | number) => request({
  url: `/workflow/instance/flowHisTaskList/${businessId}?t${Math.random()}`,
  method: 'get',
});

/**
 * 分页查询当前登录人单据
 * @param query
 * @returns {*}
 */
export const pageByCurrent = async (query: FlowInstanceQuery): AxiosPromise<FlowInstanceVO[]> => request({
  url: '/workflow/instance/pageByCurrent',
  method: 'get',
  params: query,
});

/**
 * 撤销流程
 * @param data 参数
 * @returns
 */
export const cancelProcessApply = async (data: Record<string, unknown>) => request({
  url: '/workflow/instance/cancelProcessApply',
  method: 'put',
  data,
});

/**
 * 获取流程变量
 * @param instanceId 实例id
 * @returns
 */
export const instanceVariable = async (instanceId: string | number) => request({
  url: `/workflow/instance/instanceVariable/${instanceId}`,
  method: 'get',
});

/**
 * 删除
 * @param instanceIds 流程实例id
 * @returns
 */
export const deleteByInstanceIds = async (instanceIds: Array<string | number> | string | number) => request({
  url: `/workflow/instance/deleteByInstanceIds/${instanceIds.toString()}`,
  method: 'delete',
});

/**
 * 删除历史流程实例
 * @param instanceIds
 */
export const deleteHisByInstanceIds = async (instanceIds: Array<string | number> | string | number) => request({
  url: `/workflow/instance/deleteHisByInstanceIds/${instanceIds.toString()}`,
  method: 'delete',
});

/**
 * 作废流程
 * @param data 参数
 * @returns
 */
export const invalid = async (data: Record<string, unknown>) => request({
  url: '/workflow/instance/invalid',
  method: 'post',
  data,
});

/**
 * 修改流程变量
 * @param data 参数
 * @returns
 */
export const updateVariable = async (data: Record<string, unknown>) => request({
  url: '/workflow/instance/updateVariable',
  method: 'put',
  data,
});
