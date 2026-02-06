<template>
  <div class="dp">
    <context-menu ref="contextMenuRef" :items="contextMenuItems"></context-menu>
    <div class="layout">
      <aside class="dp-sidebar desktop-only">
        <div class="dp-sidebar-header">
          <div class="brand">
            <div class="brand-logo"></div>
            <div>
              <div class="brand-itle">DeepSeek</div>
              <div class="brand-subtitle">{{ subtitle }}</div>
            </div>
          </div>
          <!-- <button class="btn btn-ghost btn-xs" @click="toggleTheme" :title="isDark ? '切换为浅色' : '切换为深色'">
            {{ isDark ? '🌙' : '☀️' }}
          </button> -->
        </div>
        <div class="dp-sidebar-newchat-btn btn" @click="handleNewChat">
          <span>+ 新对话</span>
        </div>
        <div style="height: 10px"></div>
        <div v-for="id in Object.keys(aggMap)" :key="id" @click="currentChatId = id" class="dp-sidebar-chat-btn btn">
          <i class="btn" @click="(evt: PointerEvent) => handleOpenContextMenu(evt, id)">· · ·</i>
          <span>{{ aggMap[id].api.states.title.value }}</span>
        </div>
      </aside>
      <main class="dp-main" :style="{ height: height + 'px' }">
        <header class="dp-main-topbar">
          <div class="dp-main-topbar-title">
            <template v-if="currentChatId">{{ aggMap[currentChatId].api.states.title.value }}</template>
            <template v-else>新对话</template>
          </div>
        </header>
        <section class="dp-main-messages">
          <div v-if="aggMap[currentChatId]">
            <template v-for="detail in aggMap[currentChatId].api.states.content">
              <div v-if="detail.creator === 'User'" class="dp-main-messages-user">
                {{ detail.text }}
              </div>
              <pre v-else-if="detail.creator === 'DeepSeek'" class="dp-main-messages-deepseek">{{ detail.text }}</pre>
            </template>
          </div>
          <div v-else>
            <div class="title"></div>
          </div>
        </section>
        <footer class="dp-main-footer">
          <div class="dp-main-footer-box">
            <textarea
              v-model="input"
              @keydown.enter.exact.prevent="aggMap[currentChatId]?.api.commands.query(input)"
              @keydown.shift.enter.exact.stop
              placeholder="输入你的问题，Enter 发送，Shift+Enter 换行"
              class="dp-main-footer-box-textarea"
              rows="1"
              ref="composer"
            ></textarea>
            <div class="dp-main-footer-box-toolbar">
              <!-- <button v-if="streaming" @click="stopStreaming" class="btn btn-gray btn-sm">停止</button> -->
              <button @click="handleQuery" class="btn btn-primary btn-sm" :disabled="!input.trim()" :class="{ disabled: !input.trim() }">发送</button>
            </div>
          </div>
        </footer>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useDifyConversationAgg, aggMap } from '@/store/modules/difyConversationAgg';
import { nanoid } from 'nanoid';
import ContextMenu from './ContextMenu.vue';
import { ContextMenuItem } from './define';
defineProps({
  subtitle: {
    type: String,
    default: '公文处理助手'
  }
});

const currentChatId = ref(undefined);
const contextMenuRef = ref();
const contextMenuItems = ref<ContextMenuItem[]>([]);
const input = ref('');
const height = ref(900);

onMounted(() => {
  resize();
});
function resize() {
  const vh = document.body.clientHeight;
  const headerHeight = document.getElementById('tags-view-container')?.parentElement.clientHeight;
  height.value = vh - headerHeight;
  console.debug('dp-main高度计算', vh, headerHeight);
}
window.addEventListener('resize', resize);
onBeforeUnmount(() => {
  window.removeEventListener('resize', resize);
});

function handleQuery() {
  if (currentChatId.value === undefined) {
    handleNewChat();
  }
  const query = input.value;
  input.value = '';
  nextTick(() => {
    aggMap[currentChatId.value].api.commands.query(query);
  });
}
function handleNewChat() {
  const id = nanoid();
  useDifyConversationAgg(id);
  nextTick(() => {
    currentChatId.value = id;
  });
}
function handleDeleteChat(id: string) {
  if (currentChatId.value === id) {
    currentChatId.value = undefined;
  }
  aggMap[id]?.api.destroy();
  delete aggMap[id];
}
function handleOpenContextMenu(evt: PointerEvent, id: string) {
  contextMenuItems.value = [
    {
      id,
      label: '删除',
      action: (id: string) => {
        console.debug('删除', id);
        handleDeleteChat(id);
      }
    }
  ];
  setTimeout(() => {
    contextMenuRef.value?.open(evt);
  });
}
</script>

<style lang="scss" scoped>
.dp,
.dp .layout {
  width: 100%;
  display: flex;
  min-height: calc(100vh - 85px);
  overflow-y: hidden;
}
.dp-sidebar {
  width: 261px;
  flex: 0 0 261px;
  height: 100%;
  overflow-y: scroll;
  background: #f9fafb;
  padding: 8px 12px;
  display: flex;
  flex-direction: column;

  &-header {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    .brand {
      display: flex;
      align-items: center;
      gap: 10px;
    }
    margin-bottom: 10px;
  }
  &-newchat-btn {
    background-color: #fff;
    border-width: 1px;
    border-color: rgba(103, 158, 254, 0);
    border-radius: 100px;
    border-style: solid;
    width: 100%;
    text-align: center;
    box-shadow:
      0 -2px 2px rgba(72, 104, 178, 0.04),
      0 2px 2px rgba(106, 111, 117, 0.09),
      0 1px 2px rgba(72, 104, 178, 0.08);
  }
  &-newchat-btn:hover {
    box-shadow:
      0 0 6px rgba(72, 104, 178, 0.06),
      0 4px 6px rgba(106, 111, 117, 0.12),
      0 3px 6px rgba(72, 104, 178, 0.12);
  }
  &-chat-btn {
    position: relative;
  }
  &-chat-btn i {
    display: block;
    position: absolute;
    top: 6px;
    right: 6px;
    width: 28px;
    height: 28px;
    border-radius: 28px;
    text-align: center;
    line-height: 28px;
  }
  &-chat-btn i:hover {
    background-color: #e5e8eb;
  }
  &-chat-btn span {
    display: inline-block;
    padding: 0 12px;
    border-style: unset;
    border-radius: 12px;
    width: 100%;
    height: 40px;
    font-size: 14px;
    line-height: 40px;
    color: #0f1115;
  }
  &-chat-btn span:hover:not(.active) {
    background-color: #f1f3f5;
  }
}

.brand-logo {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  background: linear-gradient(135deg, #3b82f6, #a855f7);
}

.dp-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  &-topbar {
    display: block;
    align-items: center;
    justify-content: space-between;
    height: 52px;
    border-bottom: 1px solid var(--border);
    background: var(--card-muted);
    backdrop-filter: blur(6px);
    padding: 0 16px;
    &-title {
      height: 100%;
      line-height: 34px;
      text-align: center;
      white-space: nowrap;
      text-overflow: ellipsis;
      max-width: 100%;
      transition-property: box-shadow, border;
      transition-duration: var(--ds-transition-duration);
      transition-timing-function: var(--ds-ease-in-out);
      border: 1px solid transparent;
      padding: 8px 12px;
      overflow: hidden;
    }
  }
  &-messages {
    flex: 1 1 auto;
    overflow: auto;
    text-align: right;
    padding: 0 50px;
    &-user {
      display: inline-block;
      padding: 10px;
      color: #0f1115;
      background-color: #edf3fe;
      border-radius: 24px;
      line-height: 24px;
    }
    &-deepseek,
    &-deepseek code {
      padding: 0;
      display: block;
      align-items: center;
      justify-content: space-between;
      border-bottom: 1px solid var(--border);
      background: var(--card-muted);
      backdrop-filter: blur(6px);
      text-align: left;
      margin: 25px 0;
      white-space: pre-wrap; /* 保留换行/空格，但允许自动换行 */
      word-wrap: break-word; /* 兼容旧浏览器的长单词换行 */
      font-size: 16px;
      font-family: 'Microsoft YaHei', monospace;
    }
  }
  &-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 52px;
    border-top: 1px solid var(--border);
    background: var(--card-muted);
    backdrop-filter: blur(6px);
    padding: 0 16px;
  }
}

.dp-main-footer {
  width: 100%;
  &-box {
    height: 100%;
    width: 100%;
    display: flex;
    &-textarea {
      display: block;
      padding: 12px;
      border-width: 1px;
      border-style: solid;
      border-color: rgba(0, 0, 0, 0.1);
      border-radius: 12px;
      width: calc(100% - 90px);
      outline: none;
      resize: none;
    }
    &-toolbar {
      display: flex;
      align-items: center;
      width: 90px;
      button {
        width: 100%;
        height: 36px;
        background-color: #5381f3;
        color: #fff;
        border: none;
        border-radius: 5px;
      }
    }
  }
}

.btn {
  cursor: pointer;
  user-select: none;
}
.btn[disabled] {
  cursor: not-allowed;
}
</style>
