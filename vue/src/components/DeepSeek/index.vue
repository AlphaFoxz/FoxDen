<template>
  <iframe :src="innerSrc" frameborder="0" :style="{ width: '100%', height: height + 'px' }" allow="microphone"></iframe>
</template>

<script setup lang="ts">
defineProps({
  innerSrc: {
    type: String,
    required: true
  }
});

const height = ref(900);
onMounted(() => {
  resize();
});
function resize() {
  const vh = document.body.clientHeight;
  const headerHeight = document.getElementById('tags-view-container')?.parentElement.clientHeight;
  height.value = vh - headerHeight - 17;
  console.debug('iframe高度计算', vh, headerHeight);
}
window.addEventListener('resize', resize);
onBeforeUnmount(() => {
  window.removeEventListener('resize', resize);
});
</script>
