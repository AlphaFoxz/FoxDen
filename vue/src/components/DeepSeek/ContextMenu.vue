<template>
  <teleport :to="to">
    <div v-show="isShow" id="contextMenuInput" class="context-menu" :style="styles">
      <input v-show="false" @blur="handleClose"></input>
      <div class="context-menu-item btn" v-for="item in items" :key="item.id" @click="handleClick(item)">
        {{ item.label }}
      </div>
    </div>
  </teleport>
</template>

<script setup lang="ts">
import { ContextMenuItem } from './define';
const props = defineProps({
  items: { type: Object, required: true }
});

const isShow = ref(false);
const to = ref('body')
const styles = ref({ left: '0px', top: '0px' });

onMounted(() => {
  document.addEventListener('click', handleClose);
})
onBeforeUnmount(() => {
  document.removeEventListener('click', handleClose);
})

function handleClick(item: ContextMenuItem) {
  item.action(item.id);
}
function handleOpen(event: PointerEvent) {
  console.debug('contextMenu:open', event);
  styles.value = { left: event.pageX + 'px', top: event.pageY + 'px' };
  isShow.value = true;
  nextTick(() => {
    document.getElementById('contextMenuInput')?.focus();
  });
}
function handleClose(event?: PointerEvent) {
  if(event?.target === document.getElementById('contextMenuInput')){
    return
  }
  console.debug('contextMenu:close');
  isShow.value = false;
}

defineExpose({
  open: handleOpen,
  close: handleClose
});
</script>

<style scoped lang="scss">
.context-menu {
  z-index: 100;
  position: fixed;
  width: 126px;
  padding: 6px;
  border-radius: 20px;
  background-color: #fff;
  border-style: solid;
  border-width: 1px;
  border-color: rgba(72, 104, 178, 0.2);
  box-shadow:
    0 -2px 2px rgba(72, 104, 178, 0.04),
    0 2px 2px rgba(106, 111, 117, 0.09),
    0 1px 2px rgba(72, 104, 178, 0.08);

  &-item {
    border-radius: 14px;
    height: 40px;
    line-height: 40px;
    text-align: center;
  }
  &-item:hover {
    background-color: #f2f3f4;
  }
}
.btn {
  cursor: pointer;
  user-select: none;
}
</style>
