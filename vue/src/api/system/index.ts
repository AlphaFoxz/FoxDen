import request from '@/utils/request';

export async function queryEnterpriseCount() {
  return request('/backIndex/queryEnterpriseCount', {
    method: 'get'
  });
}

export async function queryAffairsCount() {
  return request('/system/dify/queryAffairsCount', {
    method: 'post'
  });
}

export async function queryKnowledgeCount() {
  return request('/system/dify/queryKnowledgeCount', {
    method: 'post'
  });
}

export async function queryTrainingCount() {
  return request('/backIndex/queryTrainingCount', {
    method: 'get'
  });
}

export async function deptTreeSelect() {
  return request('/backIndex/deptTree', {
    method: 'get'
  });
}

export async function queryTrainingDetailPage() {
  return request('', {});
}
