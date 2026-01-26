import {ref} from 'vue';
import {createSingletonAgg} from 'vue-fn/domain';
import * as loginApi from '@/api/login';
import type {LoginData} from '@/api/login/types';
import safePromise from '@/utils/safePromise';
import {getToken} from '@/utils/auth';

const agg = createSingletonAgg(() => {
  const token = ref(getToken());
  const name = ref('');
  const nickname = ref('');
  const userId = ref<string | number>('');
  const tenantId = ref<string>('');
  const avatar = ref('');
  const roles = ref<string[]>([]); // 用户角色编码集合 → 判断路由权限
  const permissions = ref<string[]>([]); // 用户权限编码集合 → 判断按钮权限
  return {
    states: {
      token,
      name,
      nickname,
      userId,
      tenantId,
      avatar,
      roles,
      permissions,
    },
    commands: {
      setToken(v: string) {
        token.value = v;
      },
      async login(userInfo: LoginData): Promise<void> {
        const [result, error] = await safePromise(loginApi.login(userInfo));
        if (result) {
          const {data} = result;
          this.setToken(data.access_token);
          token.value = data.access_token;
          return;
        }

        throw error;
      },
    },
  };
});

export function useUserAgg() {
  return agg.api;
}
