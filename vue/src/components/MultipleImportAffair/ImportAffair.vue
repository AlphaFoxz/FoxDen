<template>
  <el-row>
    <el-col :span="24">
      <el-form ref="sendAffairTaskFormRef" :rules="rules" label-width="80px">
        <el-upload
          class="draggable-upload-container"
          drag
          v-model:file-list="fileList"
          method="post"
          :limit="10"
          :multiple="true"
          :headers="requestHeaders"
          :action="uploadKnowledgeUrl"
          @success="handleImportedKnowledge"
          :before-upload="handleBeforeUpload"
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
import UploadIcon from './UploadIcon.vue';
import { useMultipleImportAffairAgg } from './domain';
import { KnowledgeDomain, KnowledgeVo } from '@/api/system/knowledge';
import { AxiosResponse } from 'axios';

type Props = {
  proxy: ComponentInternalInstance['proxy'];
};
const props = defineProps<Props>();
const emits = defineEmits(['submit', 'cancel']);

const multipleImportAffairAgg = useMultipleImportAffairAgg();
const requestHeaders = ref(globalHeaders());
const rules = ref({});
const fileList = ref([]);
const baseUrl = import.meta.env.VITE_APP_BASE_API;
const uploadKnowledgeUrl = computed(() => {
  return `${baseUrl}/system/knowledge/add?knowledgeDomain=${KnowledgeDomain.AFFAIR}`;
});
const currentTab = ref(0);
const handleBeforeUpload = (file: any, row?: KnowledgeVo) => {
  if (multipleImportAffairAgg.states.affairList.value.length >= 10) {
    props.proxy?.$modal.msgError('最多上传10个文件');
    return false;
  }
  const fileType = ['pdf', 'docx'];
  // 校检文件类型
  if (fileType.length) {
    const fileName = file.name.split('.');
    const fileExt = fileName[fileName.length - 1];
    const isTypeOk = fileType.indexOf(fileExt) >= 0;
    if (!isTypeOk) {
      props.proxy?.$modal.msgError(`文件格式不正确, 请上传${fileType.join('/')}格式文件!`);
      return false;
    }
  }
  // 校检文件名是否包含特殊字符
  if (file.name.includes(',')) {
    props.proxy?.$modal.msgError('文件名不正确，不能包含英文逗号!');
    return false;
  }
  // 校检文件大小
  // if (props.fileSize) {
  const isLt = file.size / 1024 / 1024 < 10;
  if (!isLt) {
    props.proxy?.$modal.msgError(`上传文件大小不能超过 ${10} MB!`);
    return false;
  }
  // }
  props.proxy?.$modal.loading('正在上传文件，请稍候...');
  multipleImportAffairAgg.commands.appendFile(file);
  return true;
};
function handleImportedKnowledge(res: AxiosResponse<KnowledgeVo>) {
  console.debug('上传知识库文件', res);
  props.proxy.$modal.closeLoading();
  if (res.code === 200) {
    props.proxy.$modal.msgSuccess('上传成功');
    multipleImportAffairAgg.commands.explaned(res.data.id, res.data.title, res.data.affairOutline);
  } else {
    props.proxy.$modal.msgError('上传失败');
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
:deep(.el-upload-list__item) {
  padding-bottom: 5px;
}
</style>
