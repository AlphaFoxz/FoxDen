import {type AxiosPromise} from 'axios';
import {
  type DbTableQuery, type DbTableVO, type TableQuery, type TableVO, type GenTableVO, type DbTableForm,
} from './types';
import request from '@/utils/request';

// 查询生成表数据
export const listTable = async (query: TableQuery): AxiosPromise<TableVO[]> => request({
  url: '/tool/gen/list',
  method: 'get',
  params: query,
});

// 查询db数据库列表
export const listDbTable = async (query: DbTableQuery): AxiosPromise<DbTableVO[]> => request({
  url: '/tool/gen/db/list',
  method: 'get',
  params: query,
});

// 查询表详细信息
export const getGenTable = async (tableId: string | number): AxiosPromise<GenTableVO> => request({
  url: '/tool/gen/' + tableId,
  method: 'get',
});

// 修改代码生成信息
export const updateGenTable = async (data: DbTableForm): AxiosPromise<GenTableVO> => request({
  url: '/tool/gen',
  method: 'put',
  data,
});

// 导入表
export const importTable = async (data: {tables: string; dataName: string}): AxiosPromise<GenTableVO> => request({
  url: '/tool/gen/importTable',
  method: 'post',
  params: data,
});

// 预览生成代码
export const previewTable = async (tableId: string | number) => request({
  url: '/tool/gen/preview/' + tableId,
  method: 'get',
});

// 删除表数据
export const delTable = async (tableId: string | number | Array<string | number>) => request({
  url: '/tool/gen/' + tableId.toString(),
  method: 'delete',
});

// 生成代码（自定义路径）
export const genCode = async (tableId: string | number) => request({
  url: '/tool/gen/genCode/' + tableId,
  method: 'get',
});

// 同步数据库
export const synchDb = async (tableId: string | number) => request({
  url: '/tool/gen/synchDb/' + tableId,
  method: 'get',
});

// 获取数据源名称
export const getDataNames = async () => request({
  url: '/tool/gen/getDataNames',
  method: 'get',
});
