import {type AxiosPromise} from 'axios';
import {type DeptTreeVO} from '../dept/types';
import {type PostForm, type PostQuery, type PostVO} from './types';
import request from '@/utils/request';

// 查询岗位列表
export async function listPost(query: PostQuery): AxiosPromise<PostVO[]> {
  return request({
    url: '/system/post/list',
    method: 'get',
    params: query,
  });
}

// 查询岗位详细
export async function getPost(postId: string | number): AxiosPromise<PostVO> {
  return request({
    url: '/system/post/' + postId,
    method: 'get',
  });
}

// 获取岗位选择框列表
export async function optionselect(deptId?: number | string, postIds?: Array<number | string>): AxiosPromise<PostVO[]> {
  return request({
    url: '/system/post/optionselect',
    method: 'get',
    params: {
      postIds,
      deptId,
    },
  });
}

// 新增岗位
export async function addPost(data: PostForm) {
  return request({
    url: '/system/post',
    method: 'post',
    data,
  });
}

// 修改岗位
export async function updatePost(data: PostForm) {
  return request({
    url: '/system/post',
    method: 'put',
    data,
  });
}

// 删除岗位
export async function delPost(postId: string | number | Array<string | number>) {
  return request({
    url: '/system/post/' + postId.toString(),
    method: 'delete',
  });
}

/**
 * 查询部门下拉树结构
 */
export const deptTreeSelect = async (): AxiosPromise<DeptTreeVO[]> => request({
  url: '/system/post/deptTree',
  method: 'get',
});
