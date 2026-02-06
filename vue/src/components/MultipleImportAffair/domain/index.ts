import { createSingletonAgg, createBroadcastEvent } from 'vue-fn/domain';

export type Module = 'SendAffair' | 'ImportAffair';
const sendAffairAgg = createSingletonAgg(() => {
  const module = ref<Module>('SendAffair');
  const allowedUploadSuffix = ref(['pdf', 'doc', 'docx']);
  const title = ref<string>('');
  const affairIds = ref<Array<string | number>>([]);
  const affairOutline = ref<string>('');
  const uploadedFiles = ref<File[]>([]);
  return {
    states: {
      module,
      allowedUploadSuffix,
      title,
      affairIds,
      affairOutline,
      uploadedFiles
    },
    commands: {
      setAllowedUploadSuffix(v: string[]) {
        allowedUploadSuffix.value = v;
      },
      setTitle(v: string) {
        title.value = v;
      },
      setAffairOutline(v: string) {
        affairOutline.value = v;
      },
      appendFile(file: File) {
        uploadedFiles.value.push(file);
      },
      clearFiles() {
        uploadedFiles.value = [];
      },
      addId(id: string | number) {
        affairIds.value.push(id);
      },
      reset() {
        title.value = '';
        affairIds.value = [];
        affairOutline.value = '';
        uploadedFiles.value = [];
      }
    }
  };
});

type AffairInfo = {
  title: string;
  affairOutline: string;
};
const importAffairAgg = createSingletonAgg(() => {
  const module = ref<Module>('ImportAffair');
  const allowedUploadSuffix = ref(['pdf', 'doc', 'docx']);
  const affairList = ref<AffairInfo[]>([]);
  const uploadedFiles = ref<File[]>([]);
  const onExplanSingleAffair = createBroadcastEvent<{ id: number | string; title: string; affairOutline: string }>();
  return {
    states: {
      module,
      allowedUploadSuffix,
      affairList,
      uploadedFiles
    },
    commands: {
      setAllowedUploadSuffix(v: string[]) {
        allowedUploadSuffix.value = v;
      },
      appendFile(file: File) {
        uploadedFiles.value.push(file);
      },
      explaned(id: number | string, title: string, affairOutline: string) {
        affairList.value.push({ title, affairOutline });
        onExplanSingleAffair.publish({ id, title, affairOutline });
      },

      reset() {
        affairList.value = [];
        uploadedFiles.value = [];
      }
    }
  };
});

export function useMultipleImportAffairAgg() {
  return importAffairAgg.api;
}

export function useMultipleSendAffairAgg() {
  return sendAffairAgg.api;
}
