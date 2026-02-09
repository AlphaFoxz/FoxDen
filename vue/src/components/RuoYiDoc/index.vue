<template>
  <div>
    <svg-icon icon-class="question" @click="goto" />
  </div>
</template>

<script setup lang="ts">
const url = ref('https://github.com/AlphaFoxz/FoxDen/blob/main/README.md');

function goto() {
  window.open(url.value);
}
</script>
