import {type AxiosPromise} from 'axios';
import request from '@/utils/request';
import {
  type CategoryVO, type CategoryForm, type CategoryQuery, type CategoryTreeVO,
} from '@/api/workflow/category/types';

/**
 * 查询流程分类列表
 * @param query
 * @returns {*}
 */

export const listCategory = async (query?: CategoryQuery): AxiosPromise<CategoryVO[]> => request({
  url: '/workflow/category/list',
  method: 'get',
  params: query,
});

/**
 * 查询流程分类详细
 * @param categoryId
 */
export const getCategory = async (categoryId: string | number): AxiosPromise<CategoryVO> => request({
  url: '/workflow/category/' + categoryId,
  method: 'get',
});

/**
 * 新增流程分类
 * @param data
 */
export const addCategory = async (data: CategoryForm) => request({
  url: '/workflow/category',
  method: 'post',
  data,
});

/**
 * 修改流程分类
 * @param data
 */
export const updateCategory = async (data: CategoryForm) => request({
  url: '/workflow/category',
  method: 'put',
  data,
});

/**
 * 删除流程分类
 * @param categoryId
 */
export const delCategory = async (categoryId: string | number | Array<string | number>) => request({
  url: '/workflow/category/' + categoryId.toString(),
  method: 'delete',
});

/**
 * 获取流程分类树列表
 * @param query 流程实例id
 * @returns
 */
export const categoryTree = async (query?: CategoryForm): AxiosPromise<CategoryTreeVO[]> => request({
  url: '/workflow/category/categoryTree',
  method: 'get',
  params: query,
});
