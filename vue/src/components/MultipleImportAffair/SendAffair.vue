<template>
  <el-row>
    <el-col :span="24">
      <el-form ref="sendAffairTaskFormRef" :rules="rules" label-width="80px">
        <el-upload
          class="draggable-upload-container"
          drag
          v-model:file-list="fileList"
          list-type="text"
          method="post"
          :headers="requestHeaders"
          :limit="10"
          :multiple="true"
          :action="uploadTaskAffairUrl"
          @success="handleUploadSuccess"
          :before-upload="beforeUpload"
        >
          <el-row>
            <el-col class="uploader-info">
              <div class="svg-wrapper">
                <UploadIcon width="50" height="50"></UploadIcon>
              </div>
            </el-col>
          </el-row>
          <el-row>
            <el-col class="uploader-info">
              <el-space>将文件拖拽至此 或 点击 上传</el-space>
            </el-col>
          </el-row>
          <el-row>
            <el-col class="uploader-desc">
              <el-space>支持.docx,.doc格式，最大10MB</el-space>
            </el-col>
          </el-row>
        </el-upload>
      </el-form>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { globalHeaders } from '@/utils/request';
import { UploadRawFile } from 'element-plus';
import UploadIcon from './UploadIcon.vue';
import { useMultipleSendAffairAgg } from './domain';

type Props = {
  proxy: ComponentInternalInstance['proxy'];
};
const props = defineProps<Props>();

const multipleSendAffairAgg = useMultipleSendAffairAgg();
const requestHeaders = ref(globalHeaders());

const fileList = ref([]);
const currentFile = ref();
const uploadIds = ref<string[]>([]);
const parsing = ref(false);
const parseComplete = ref(false);
const rules = ref({});
const baseUrl = import.meta.env.VITE_APP_BASE_API;
const uploadTaskAffairUrl = computed(() => {
  return baseUrl + '/system/sendAffairTask/uploadTaskAffairFile';
});

// 通知父组件上传成功和解析状态
const emit = defineEmits(['uploadSuccess', 'parseComplete']);

function beforeUpload(file: UploadRawFile) {
  if (
    !multipleSendAffairAgg.states.allowedUploadSuffix.value.includes(
      file.name
        .split('.')
        .findLast(() => true)
        ?.toLowerCase()
    )
  ) {
    props.proxy?.$modal.msgError('文件格式错误，请上传图片类型,如：JPG，PNG后缀的文件。');
    return false;
  }
  const isLt = file.size / 1024 / 1024 < 20;
  if (!isLt) {
    props.proxy.$modal.msgError(`上传文件大小不能超过 ${20} MB!`);
    return false;
  }
  const reader = new FileReader();
  reader.readAsDataURL(file);
  props.proxy.$modal.loading('正在上传文件，请稍候...');
  reader.onload = () => {
    // const nameArr = file.name.split('.');
    // nameArr.pop();
    // multipleSendAffairAgg.commands.setTitle(nameArr.join('.'));
    currentFile.value = file;
    // 重置状态
    // uploadId.value = '';
    parseComplete.value = true;
    // 通知父组件文件已选择
    emit('uploadSuccess', file);
    emit('parseComplete', true);
  };
}

function handleUploadSuccess(res: any, file: any, uploadFileList: any[]) {
  console.debug('上传成功', res);
  props.proxy.$modal.closeLoading();
  if (res.code === 200 && res.data?.url) {
    console.debug('上传结果', res.data);
    uploadIds.value.push(res.data.url);
    multipleSendAffairAgg.commands.addId(res.data.affairId);
    props.proxy.$modal.msgSuccess('文件上传成功，请点击"解析政策"按钮进行解析');
  } else {
    props.proxy.$modal.msgError(res.data?.message || '上传失败');
  }
}
</script>

<style scoped lang="scss">
.draggable-upload-container {
  width: 100%;
  &-display {
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
  }
}

:deep(.el-upload-dragger) {
  border-width: initial;
  .svg-wrapper {
    background-color: #c6d5ff;
    padding: 5px;
    margin-bottom: 10px;
    border-radius: 50%;
  }
  .uploader-desc {
    font-size: 12px;
    color: #999;
  }
  .uploader-info {
    display: flex;
    flex-direction: column;
    align-items: center;
    color: #555;
  }
}
:deep(.el-upload:hover) {
  .uploader-info {
    color: var(--el-color-primary);
  }
}
</style>
