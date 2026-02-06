import axios, {type AxiosResponse, type InternalAxiosRequestConfig} from 'axios';
import {type LoadingInstance} from 'element-plus/es/components/loading/src/loading';
import FileSaver from 'file-saver';
import {useUserStore} from '@/store/modules/user';
import {getToken} from '@/utils/auth';
import {tansParams, blobValidate} from '@/utils/ruoyi';
import cache from '@/plugins/cache';
import {HttpStatus} from '@/enums/RespEnum';
import {errorCode} from '@/utils/errorCode';
import {getLanguage} from '@/lang';
import {
  encryptBase64, encryptWithAes, generateAesKey, decryptWithAes, decryptBase64,
} from '@/utils/crypto';
import {encrypt, decrypt} from '@/utils/jsencrypt';
import router from '@/router';

const encryptHeader = 'encrypt-key';
let downloadLoadingInstance: LoadingInstance;
// 是否显示重新登录
export const isRelogin = {show: false};
export const globalHeaders = () => ({
  Authorization: 'Bearer ' + getToken(),
  clientid: import.meta.env.VITE_APP_CLIENT_ID,
});

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8';
axios.defaults.headers.clientid = import.meta.env.VITE_APP_CLIENT_ID;
// 创建 axios 实例
const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 50_000,
  transitional: {
    // 超时错误更明确
    clarifyTimeoutError: true,
  },
});

// 请求拦截器
service.interceptors.request.use(
  async (config: InternalAxiosRequestConfig) => {
    // 对应国际化资源文件后缀
    config.headers['Content-Language'] = getLanguage();

    const isToken = config.headers?.isToken === false;
    // 是否需要防止数据重复提交
    const isRepeatSubmit = config.headers?.repeatSubmit === false;
    // 是否需要加密
    const isEncrypt = config.headers?.isEncrypt === 'true';

    if (getToken() && !isToken) {
      config.headers.Authorization = 'Bearer ' + getToken(); // 让每个请求携带自定义token 请根据实际情况自行修改
    }

    // Get请求映射params参数
    if (config.method === 'get' && config.params) {
      let url = config.url + '?' + tansParams(config.params);
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

    if (import.meta.env.VITE_APP_ENCRYPT === 'true' // 当开启参数加密
    	&& isEncrypt && (config.method === 'post' || config.method === 'put')) {
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
  (res: AxiosResponse) => {
    if (import.meta.env.VITE_APP_ENCRYPT === 'true') {
      // 加密后的 AES 秘钥
      const keyString = res.headers[encryptHeader];
      // 加密
      if (keyString != null && keyString != '') {
        const {data} = res;
        // 请求体 AES 解密
        const base64String = decrypt(keyString);
        // Base64 解码 得到请求头的 AES 秘钥
        const aesKey = decryptBase64(base64String.toString());
        // AesKey 解码 data
        const decryptData = decryptWithAes(data, aesKey);
        // 将结果 (得到的是 JSON 字符串) 转为 JSON
        res.data = JSON.parse(decryptData);
      }
    }

    // 未设置状态码则默认成功状态
    const code = res.data.code || HttpStatus.SUCCESS;
    // 获取错误信息
    const message = errorCode[code] || res.data.msg || errorCode.default;
    // 二进制数据则直接返回
    if (res.request.responseType === 'blob' || res.request.responseType === 'arraybuffer') {
      return res.data;
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

    return Promise.resolve(res.data);
  },
  async (error: any) => {
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
// 通用下载方法
export async function download(url: string, parameters: any, fileName: string) {
  downloadLoadingInstance = ElLoading.service({text: '正在下载数据，请稍候', background: 'rgba(0, 0, 0, 0.7)'});
  // prettier-ignore
  return service.post(url, parameters, {
    transformRequest: [
      (parameters: any) => tansParams(parameters),
    ],
    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
    responseType: 'blob',
  }).then(async (resp: any) => {
    const isLogin = blobValidate(resp);
    if (isLogin) {
      const blob = new Blob([resp]);
      FileSaver.saveAs(blob, fileName);
    } else {
      const blob = new Blob([resp]);
      const resText = await blob.text();
      const rspObject = JSON.parse(resText);
      const errorMessage = errorCode[rspObject.code] || rspObject.msg || errorCode.default;
      ElMessage.error(errorMessage);
    }

    downloadLoadingInstance.close();
  }).catch((error: any) => {
    console.error(error);
    ElMessage.error('下载文件出现错误，请联系管理员！');
    downloadLoadingInstance.close();
  });
}

// 导出 axios 实例
export default service;
