import {type AxiosPromise} from 'axios';
import request from '@/utils/request';
import {
  type FlowDefinitionQuery, type definitionXmlVO, type FlowDefinitionForm, type FlowDefinitionVo,
} from '@/api/workflow/definition/types';

/**
 * 获取流程定义列表
 * @param query 流程实例id
 * @returns
 */
export const listDefinition = async (query: FlowDefinitionQuery): AxiosPromise<FlowDefinitionVo[]> => request({
  url: '/workflow/definition/list',
  method: 'get',
  params: query,
});

/**
 * 查询未发布的流程定义列表
 * @param query 流程实例id
 * @returns
 */
export const unPublishList = async (query: FlowDefinitionQuery): AxiosPromise<FlowDefinitionVo[]> => request({
  url: '/workflow/definition/unPublishList',
  method: 'get',
  params: query,
});

/**
 * 通过流程定义id获取xml
 * @param definitionId 流程定义id
 * @returns
 */
export const definitionXml = async (definitionId: string): AxiosPromise<definitionXmlVO> => request({
  url: `/workflow/definition/definitionXml/${definitionId}`,
  method: 'get',
});

/**
 * 删除流程定义
 * @param id 流程定义id
 * @returns
 */
export const deleteDefinition = async (id: string | string[]) => request({
  url: '/workflow/definition/' + id.toString(),
  method: 'delete',
});

/**
 * 挂起/激活
 * @param definitionId 流程定义id
 * @param activityStatus 状态
 * @returns
 */
export const active = async (definitionId: string, activityStatus: boolean) => request({
  url: `/workflow/definition/active/${definitionId}`,
  method: 'put',
  params: {
    active: activityStatus,
  },
});

/**
 * 通过zip或xml部署流程定义
 * @returns
 */
export async function importDef(data: Record<string, unknown>) {
  return request({
    url: '/workflow/definition/importDef',
    method: 'post',
    data,
    headers: {
      repeatSubmit: false,
    },
  });
}

/**
 * 发布流程定义
 * @param id 流程定义id
 * @returns
 */
export const publish = async (id: string) => request({
  url: `/workflow/definition/publish/${id}`,
  method: 'put',
});

/**
 * 取消发布流程定义
 * @param id 流程定义id
 * @returns
 */
export const unPublish = async (id: string) => request({
  url: `/workflow/definition/unPublish/${id}`,
  method: 'put',
});

/**
 * 获取流程定义xml字符串
 * @param id 流程定义id
 * @returns
 */
export const xmlString = async (id: string) => request({
  url: `/workflow/definition/xmlString/${id}`,
  method: 'get',
});

/**
 * 新增
 * @param data 参数
 * @returns
 */
export const add = async (data: FlowDefinitionForm) => request({
  url: '/workflow/definition',
  method: 'post',
  data,
});

/**
 * 修改
 * @param data 参数
 * @returns
 */
export const edit = async (data: FlowDefinitionForm) => request({
  url: '/workflow/definition',
  method: 'put',
  data,
});

/**
 * 查询详情
 * @param id 参数
 * @returns
 */
export const getInfo = async (id: number | string) => request({
  url: `/workflow/definition/${id}`,
  method: 'get',
});

/**
 * 复制流程定义
 * @param id 流程定义id
 * @returns
 */
export const copy = async (id: string) => request({
  url: `/workflow/definition/copy/${id}`,
  method: 'post',
});
