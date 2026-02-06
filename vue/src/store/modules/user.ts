import {to} from 'await-to-js';
import {defineStore} from 'pinia';
import {ref} from 'vue';
import {getToken, removeToken, setToken} from '@/utils/auth';
import {login as loginApi, logout as logoutApi, getInfo as getUserInfo} from '@/api/login';
import {type LoginData} from '@/api/types';
import defAva from '@/assets/images/profile.jpg';

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken());
  const name = ref('');
  const nickname = ref('');
  const userId = ref<string | number>('');
  const tenantId = ref<string>('');
  const avatar = ref('');
  const roles = ref<string[]>([]); // 用户角色编码集合 → 判断路由权限
  const permissions = ref<string[]>([]); // 用户权限编码集合 → 判断按钮权限

  /**
   * 登录
   * @param userInfo
   * @returns
   */
  const login = async (userInfo: LoginData): Promise<void> => {
    const [error, res] = await to(loginApi(userInfo));
    if (res) {
      const {data} = res;
      setToken(data.access_token);
      token.value = data.access_token;
      return;
    }

    throw error;
  };

  // 获取用户信息
  const getInfo = async (): Promise<void> => {
    const [error, res] = await to(getUserInfo());
    if (res) {
      const {data} = res;
      const {user} = data;
      const profile = user.avatar == '' || user.avatar == null ? defAva : user.avatar;

      if (data.roles && data.roles.length > 0) {
        // 验证返回的roles是否是一个非空数组
        roles.value = data.roles;
        permissions.value = data.permissions;
      } else {
        roles.value = ['ROLE_DEFAULT'];
      }

      name.value = user.userName;
      nickname.value = user.nickName;
      avatar.value = profile;
      userId.value = user.userId;
      tenantId.value = user.tenantId;
      return;
    }

    throw error;
  };

  // 注销
  const logout = async (): Promise<void> => {
    await logoutApi();
    token.value = '';
    roles.value = [];
    permissions.value = [];
    removeToken();
  };

  const setAvatar = (value: string) => {
    avatar.value = value;
  };

  return {
    userId,
    tenantId,
    token,
    nickname,
    avatar,
    roles,
    permissions,
    login,
    getInfo,
    logout,
    setAvatar,
  };
});
