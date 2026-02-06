<template>
  <div v-if="mount">
    <Teleport v-if="!noPadding" to=".app-main">
      <div style="height: 50px"></div>
    </Teleport>
    <div class="fixed-bar">
      <el-footer style="z-index: 1000; background: #fff">
        <pagination v-show="total > 0" :total="total" v-model:page="page" v-model:limit="limit" @pagination="$emit('pagination')" />
      </el-footer>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  total: number;
  noPadding?: boolean;
}>();
const mount = ref(false);
onMounted(() => {
  setTimeout(() => {
    mount.value = true;
  });
});
const page = defineModel<number>('page');
const limit = defineModel<number>('limit');
defineEmits<{
  pagination: [];
}>();
</script>

<style scoped>
.fixed-bar {
  position: fixed;
  right: 0;
  bottom: 0;
  width: 100%;
  z-index: 1000;
}
.footer {
  position: fixed !important;
  left: 0;
  bottom: 0;
  width: 100%;
}
</style>
