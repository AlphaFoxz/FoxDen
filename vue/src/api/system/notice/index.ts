import {type AxiosPromise} from 'axios';
import {type NoticeForm, type NoticeQuery, type NoticeVO} from './types';
import request from '@/utils/request';
// 查询公告列表
export async function listNotice(query: NoticeQuery): AxiosPromise<NoticeVO[]> {
  return request({
    url: '/system/notice/list',
    method: 'get',
    params: query,
  });
}

// 查询公告详细
export async function getNotice(noticeId: string | number): AxiosPromise<NoticeVO> {
  return request({
    url: '/system/notice/' + noticeId,
    method: 'get',
  });
}

// 新增公告
export async function addNotice(data: NoticeForm) {
  return request({
    url: '/system/notice',
    method: 'post',
    data,
  });
}

// 修改公告
export async function updateNotice(data: NoticeForm) {
  return request({
    url: '/system/notice',
    method: 'put',
    data,
  });
}

// 删除公告
export async function delNotice(noticeId: string | number | Array<string | number>) {
  return request({
    url: '/system/notice/' + noticeId.toString(),
    method: 'delete',
  });
}
