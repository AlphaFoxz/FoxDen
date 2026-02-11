import {type AxiosPromise} from 'axios';
import {type CacheVO} from './types';
import request from '@/utils/request';

// 查询缓存详细
export async function getCache(): AxiosPromise<CacheVO> {
  return request({
    url: '/monitor/cache',
    method: 'get',
  });
}

// 查询缓存名称列表
export async function listCacheName() {
  return request({
    url: '/monitor/cache/getNames',
    method: 'get',
  });
}

// 查询缓存键名列表
export async function listCacheKey(cacheName: string) {
  return request({
    url: '/monitor/cache/getKeys/' + cacheName,
    method: 'get',
  });
}

// 查询缓存内容
export async function getCacheValue(cacheName: string, cacheKey: string) {
  return request({
    url: '/monitor/cache/getValue/' + cacheName + '/' + cacheKey,
    method: 'get',
  });
}

// 清理指定名称缓存
export async function clearCacheName(cacheName: string) {
  return request({
    url: '/monitor/cache/clearCacheName/' + cacheName,
    method: 'delete',
  });
}

// 清理指定键名缓存
export async function clearCacheKey(cacheName: string, cacheKey: string) {
  return request({
    url: '/monitor/cache/clearCacheKey/' + cacheName + '/' + cacheKey,
    method: 'delete',
  });
}

// 清理全部缓存
export async function clearCacheAll() {
  return request({
    url: '/monitor/cache/clearCacheAll',
    method: 'delete',
  });
}
