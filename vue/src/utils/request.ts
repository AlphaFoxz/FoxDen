import axios, {type AxiosResponse, type InternalAxiosRequestConfig} from 'axios';
import {decrypt, encrypt} from './jsencrypt';
import {
  decryptBase64, decryptWithAes, encryptBase64, encryptWithAes, generateAesKey,
} from './crypto';
import {getToken} from './auth';
import {transParameters} from './framework';
import {useLanguageAgg} from '@/domain/language';

const languageAgg = useLanguageAgg();

const encryptHeader = 'encrypt-key';
// 是否显示重新登录
export const isRelogin = {show: false};
export const globalHeaders = () => ({
  Authorization: 'Bearer ' + (getToken() ?? ''),
  clientid: import.meta.env.VITE_APP_CLIENT_ID,
});

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8';
axios.defaults.headers.clientid = import.meta.env.VITE_APP_CLIENT_ID;
// 创建 axios 实例
const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 120_000,
});

// 请求拦截器
service.interceptors.request.use(
  async (config: InternalAxiosRequestConfig) => {
    // 对应国际化资源文件后缀
    config.headers['Content-Language'] = languageAgg.states.currentLanguage.value;

    const isToken = config.headers?.isToken === false;
    // 是否需要防止数据重复提交
    const isRepeatSubmit = config.headers?.repeatSubmit === false;
    // 是否需要加密
    const isEncrypt = config.headers?.isEncrypt === 'true';

    if (getToken() && !isToken) {
      config.headers.Authorization = 'Bearer ' + getToken(); // 让每个请求携带自定义token 请根据实际情况自行修改
    }

    // Get请求映射params参数
    if (config.method === 'get' && typeof config.params === 'object') {
      let url = config.url + '?' + transParameters(config.params as Record<string, unknown>);
      url = url.slice(0, -1);
      config.params = {};
      config.url = url;
    }

    if (!isRepeatSubmit && (config.method === 'post' || config.method === 'put')) {
      const requestObject = {
        url: config.url,
        data: typeof config.data === 'object' ? JSON.stringify(config.data) : config.data,
        time: Date.now(),
      };
      const sessionObject = cache.session.getJSON('sessionObj');
      if (sessionObject === undefined || sessionObject === null || sessionObject === '') {
        cache.session.setJSON('sessionObj', requestObject);
      } else {
        const s_url = sessionObject.url; // 请求地址
        const s_data = sessionObject.data; // 请求数据
        const s_time = sessionObject.time; // 请求时间
        const interval = 500; // 间隔时间(ms)，小于此时间视为重复提交
        if (s_data === requestObject.data && requestObject.time - s_time < interval && s_url === requestObject.url) {
          const message = '数据正在处理，请勿重复提交';
          console.warn(`[${s_url}]: ` + message);
          throw new Error(message);
        }

        cache.session.setJSON('sessionObj', requestObject);
      }
    }

    if (import.meta.env.VITE_APP_ENCRYPT === 'true' /* 当开启参数加密 */&& isEncrypt && (config.method === 'post' || config.method === 'put')) {
      // 生成一个 AES 密钥
      const aesKey = generateAesKey();
      config.headers[encryptHeader] = encrypt(encryptBase64(aesKey));
      config.data = typeof config.data === 'object' ? encryptWithAes(JSON.stringify(config.data), aesKey) : encryptWithAes(config.data, aesKey);
    }

    // FormData数据去请求头Content-Type
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type'];
    }

    return config;
  },
  async (error: any) => {
    throw error;
  },
);

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    if (import.meta.env.VITE_APP_ENCRYPT === 'true') {
      // 加密后的 AES 秘钥
      const keyString = response.headers[encryptHeader] as (string | undefined | null);
      // 加密
      if (keyString) {
        const data = response.data as string;
        // 请求体 AES 解密
        const base64String = decrypt(keyString);
        // Base64 解码 得到请求头的 AES 秘钥
        const aesKey = decryptBase64(base64String.toString());
        // AesKey 解码 data
        const decryptData = decryptWithAes(data, aesKey);
        // 将结果 (得到的是 JSON 字符串) 转为 JSON
        response.data = JSON.parse(decryptData) as Record<string, unknown>;
      }
    }

    // 未设置状态码则默认成功状态
    const code = response.data.code ?? HttpStatus.SUCCESS;
    // 获取错误信息
    const message = errorCode[code] || response.data.msg || errorCode.default;
    // 二进制数据则直接返回
    if (response.request.responseType === 'blob' || response.request.responseType === 'arraybuffer') {
      return response.data;
    }

    if (code === 401) {
      // prettier-ignore
      if (!isRelogin.show) {
        isRelogin.show = true;
        ElMessageBox.confirm('登录状态已过期，您可以继续留在该页面，或者重新登录', '系统提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning',
        }).then(() => {
          isRelogin.show = false;
          useUserStore().logout().then(() => {
            router.replace({
              path: '/login',
              query: {
                redirect: encodeURIComponent(router.currentRoute.value.fullPath || '/'),
              },
            });
          });
        }).catch(() => {
          isRelogin.show = false;
        });
      }

      return Promise.reject('无效的会话，或者会话已过期，请重新登录。');
    }

    if (code === HttpStatus.SERVER_ERROR) {
      ElMessage({message, type: 'error'});
      return Promise.reject(new Error(message));
    }

    if (code === HttpStatus.WARN) {
      ElMessage({message, type: 'warning'});
      return Promise.reject(new Error(message));
    }

    if (code !== HttpStatus.SUCCESS) {
      ElNotification.error({title: message});
      return Promise.reject('error');
    }

    return Promise.resolve(response.data);
  },
  async (error: unknown) => {
    let {message} = error;
    if (message == 'Network Error') {
      message = '后端接口连接异常';
    } else if (message.includes('timeout')) {
      message = '系统接口请求超时';
    } else if (message.includes('Request failed with status code')) {
      message = '系统接口' + message.slice(-3) + '异常';
    }

    ElMessage({message, type: 'error', duration: 5 * 1000});
    throw error;
  },
);

export default service;

export const HttpStatus = {
  /**
   * 操作成功
   */
  SUCCESS: 200,
  /**
   * 对象创建成功
   */
  CREATED: 201,
  /**
   * 请求已经被接受
   */
  ACCEPTED: 202,
  /**
   * 操作已经执行成功，但是没有返回数据
   */
  NO_CONTENT: 204,
  /**
   * 资源已经被移除
   */
  MOVED_PERM: 301,
  /**
   * 重定向
   */
  SEE_OTHER: 303,
  /**
   * 资源没有被修改
   */
  NOT_MODIFIED: 304,
  /**
   * 参数列表错误（缺少，格式不匹配）
   */
  PARAM_ERROR: 400,
  /**
   * 未授权
   */
  UNAUTHORIZED: 401,
  /**
   * 访问受限，授权过期
   */
  FORBIDDEN: 403,
  /**
   * 资源，服务未找到
   */
  NOT_FOUND: 404,
  /**
   * 不允许的http方法
   */
  BAD_METHOD: 405,
  /**
   * 资源冲突，或者资源被锁
   */
  CONFLICT: 409,
  /**
   * 不支持的数据，媒体类型
   */
  UNSUPPORTED_TYPE: 415,
  /**
   * 系统内部错误
   */
  SERVER_ERROR: 500,
  /**
   * 接口未实现
   */
  NOT_IMPLEMENTED: 501,
  /**
   * 服务不可用，过载或者维护
   */
  BAD_GATEWAY: 502,
  /**
   * 网关超时
   */
  GATEWAY_TIMEOUT: 504,
  /**
   * 未知错误
   */
  UNKNOWN_ERROR: 520,
  /**
   * 服务未知错误
   */
  SERVICE_ERROR: 521,
  /**
   * 数据库未知错误
   */
  DATABASE_ERROR: 522,
  /**
   * 系统警告消息
   */
  WARN: 601,
} as const;

export type HttpStatus = Enum<typeof HttpStatus>;
