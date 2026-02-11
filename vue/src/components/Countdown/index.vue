<template>
  <div class="box">
    <div class="box-label">{{ label }}</div>
    <div class="box-timer">{{ formatSecondsToMMSS(leftTime / 1000) }}</div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps({
  initTimeSecond: {
    type: Number,
    default: 5,
  },
  label: {
    type: String,
    default: '倒计时',
  },
});
const emits = defineEmits(['timeOver']);

const leftTime = ref(props.initTimeSecond * 1000);
const timer = setInterval(() => {
  leftTime.value -= 1000;
  if (leftTime.value <= 0) {
    emits('timeOver');
    timer && clearInterval(timer);
  }
}, 1000);
watch(
  () => props.initTimeSecond,
  () => {
    leftTime.value = props.initTimeSecond * 1000;
  },
);
onBeforeUnmount(() => {
  timer && clearInterval(timer);
});

function formatSecondsToMMSS(totalSeconds: number): string {
  if (!Number.isFinite(totalSeconds)) {
    throw new Error('totalSeconds must be a finite number');
  }

  const s = Math.max(0, Math.floor(totalSeconds));
  const minutes = Math.floor(s / 60);
  const seconds = s % 60;

  const mm = minutes.toString().padStart(2, '0');
  const ss = seconds.toString().padStart(2, '0');

  return `${mm}:${ss}`;
}
</script>

<style scoped lang="scss">
.box {
  display: flex;
  align-items: center;
  // justify-content: center;
  flex-direction: row;

  &-label {
    margin-right: 10px;
    font-size: 14px;
    color: #666;
  }

  &-timer {
    font-size: 14px;
  }
}
</style>
