import axios from 'axios';
import FileSaver from 'file-saver';
import {type LoadingInstance} from 'element-plus/es/components/loading/src/loading';
import errorCode from '@/utils/errorCode';
import {blobValidate} from '@/utils/ruoyi';
import {globalHeaders} from '@/utils/request';

const baseURL = import.meta.env.VITE_APP_BASE_API;
let downloadLoadingInstance: LoadingInstance;
const expose = {
  async oss(ossId: string | number) {
    const url = baseURL + '/resource/oss/download/' + ossId;
    downloadLoadingInstance = ElLoading.service({text: '正在下载数据，请稍候', background: 'rgba(0, 0, 0, 0.7)'});
    try {
      const res = await axios({
        method: 'get',
        url,
        responseType: 'blob',
        headers: globalHeaders(),
      });
      const isBlob = blobValidate(res.data);
      if (isBlob) {
        const blob = new Blob([res.data], {type: 'application/octet-stream'});
        FileSaver.saveAs(blob, decodeURIComponent(res.headers['download-filename'] as string));
      } else {
        this.printErrMsg(res.data);
      }

      downloadLoadingInstance.close();
    } catch (error) {
      console.error(error);
      ElMessage.error('下载文件出现错误，请联系管理员！');
      downloadLoadingInstance.close();
    }
  },
  async zip(url: string, name: string) {
    url = baseURL + url;
    downloadLoadingInstance = ElLoading.service({text: '正在下载数据，请稍候', background: 'rgba(0, 0, 0, 0.7)'});
    try {
      const res = await axios({
        method: 'get',
        url,
        responseType: 'blob',
        headers: globalHeaders(),
      });
      const isBlob = blobValidate(res.data);
      if (isBlob) {
        const blob = new Blob([res.data], {type: 'application/zip'});
        FileSaver.saveAs(blob, name);
      } else {
        this.printErrMsg(res.data);
      }

      downloadLoadingInstance.close();
    } catch (error) {
      console.error(error);
      ElMessage.error('下载文件出现错误，请联系管理员！');
      downloadLoadingInstance.close();
    }
  },
  async printErrMsg(data: any) {
    const resText = await data.text();
    const rspObject = JSON.parse(resText);
    const errorMessage = errorCode[rspObject.code] || rspObject.msg || errorCode.default;
    ElMessage.error(errorMessage);
  },
};
export default expose;
