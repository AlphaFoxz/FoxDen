import {type AxiosPromise} from 'axios';
import request from '@/utils/request';
import {type LeaveVO, type LeaveQuery, type LeaveForm} from '@/api/workflow/leave/types';

/**
 * 查询请假列表
 * @param query
 * @returns {*}
 */

export const listLeave = async (query?: LeaveQuery): AxiosPromise<LeaveVO[]> => request({
  url: '/workflow/leave/list',
  method: 'get',
  params: query,
});

/**
 * 查询请假详细
 * @param id
 */
export const getLeave = async (id: string | number): AxiosPromise<LeaveVO> => request({
  url: '/workflow/leave/' + id,
  method: 'get',
});

/**
 * 新增请假
 * @param data
 */
export const addLeave = async (data: LeaveForm): AxiosPromise<LeaveVO> => request({
  url: '/workflow/leave',
  method: 'post',
  data,
});

/**
 * 提交请假并发起流程
 * @param data
 */
export const submitAndFlowStart = async (data: LeaveForm): AxiosPromise<LeaveVO> => request({
  url: '/workflow/leave/submitAndFlowStart',
  method: 'post',
  data,
});

/**
 * 修改请假
 * @param data
 */
export const updateLeave = async (data: LeaveForm): AxiosPromise<LeaveVO> => request({
  url: '/workflow/leave',
  method: 'put',
  data,
});

/**
 * 删除请假
 * @param id
 */
export const delLeave = async (id: string | number | Array<string | number>) => request({
  url: '/workflow/leave/' + id.toString(),
  method: 'delete',
});
