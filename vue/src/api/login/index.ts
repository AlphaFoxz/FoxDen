import type {AxiosPromise} from 'axios';
import type {LoginData, LoginResult} from './types';
import request from '@/utils/request';

// Pc端固定客户端授权id
const clientId = import.meta.env.VITE_APP_CLIENT_ID;

export async function login(data: LoginData): AxiosPromise<LoginResult> {
  const parameters = {
    ...data,
    clientId: data.clientId || clientId,
    grantType: data.grantType || 'password',
  };
  return request({
    url: '/auth/login',
    headers: {
      isToken: false,
      isEncrypt: true,
      repeatSubmit: false,
    },
    method: 'post',
    data: parameters,
  });
}
