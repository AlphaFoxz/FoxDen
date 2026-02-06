import {createBroadcastEvent, createMultiInstanceAgg, createPluginHelperByAggCreator} from 'vue-fn/domain';
import request from '@/utils/request';

export type ConversationDetail = {
  creator: 'User' | 'DeepSeek';
  status: 'Completed' | 'Cancelled' | 'Failed' | 'Running';
  text: string;
};
type DifyEvent = SSEDifyMessage | SSEDifyError | SSEDifyMeta | SSEDifyDone;
export type SSEDifyMessage = {type: 'dify.message'; id: string; value: string};
export type SSEDifyError = {type: 'dify.error'; id: string; value: string};
export type SSEDifyMeta = {type: 'dify.meta'; id: string; key: string; value: string};
export type SSEDifyDone = {type: 'dify.done'; id: string};
type QueryBo = {
  id: string;
  conversationId?: string;
  model?: string;
  query: string;
};

export const aggMap = shallowReactive<Record<string, ReturnType<typeof createAgg>>>({});
const onRegister = createBroadcastEvent<{conversationId: string}>();

function createAgg(id: string) {
  return createMultiInstanceAgg(id, () => {
    const conversationId = ref<string>(undefined);
    const title = ref<string>('新对话');
    const content = reactive<ConversationDetail[]>([
      /* { creator: 'User', status: 'Completed', 'text': '打印5行斐波那契数列' },
      {
        creator: 'DeepSeek',
        status: 'Completed',
        'text': `您好。根据您的要求，以下是斐波那契数列的前5行（每行一个数）：

1
1
2
3
5

斐波那契数列从第1项和第2项开始，每一项都是前两项的和。如果您需要更多行或有其他问题，请随时告知。`
      } */
    ]);
    const doneFlag = ref(true);

    return {
      states: {
        title,
        conversationId,
        content,
        doneFlag,
      },
      commands: {
        async query(input: string) {
          const bo: QueryBo = {
            id,
            conversationId: conversationId.value,
            query: input,
          };
          content.push({creator: 'User', status: 'Completed', text: input});
          const response = await request({
            url: '/system/dify/chatMessageWithStream',
            method: 'post',
            data: bo,
          });
          if (response.code !== 200) {
            console.error(response);
          }
        },
        setDoneFlag(v: boolean) {
          doneFlag.value = v;
        },
        setTitle(v: string) {
          title.value = v;
        },
        setConversationId(id: string) {
          conversationId.value = id;
        },
        // TODO 待实现取消功能
        // cancelCallbackAnswer() {},
        handleEvent(event: DifyEvent) {
          console.debug('dify event', event);
          if (isDifyMetaEvent(event)) {
            if (event.key === 'conversation_id') {
              conversationId.value = event.value;
            }
          } else if (isDifyMessageEvent(event)) {
            if (doneFlag.value) {
              doneFlag.value = false;
              content.push({creator: 'DeepSeek', status: 'Running', text: ''});
            }

            const step = 30;
            let delay = 0;
            for (const c of event.value.split('')) {
              setTimeout(
                () => {
                  content.at(-1).text += c;
                },
                (delay += step),
              );
            }
          } else if (isDifyErrorEvent(event)) {
            if (!doneFlag.value) {
              content.at(-1).status = 'Failed';
              doneFlag.value = true;
            }

            console.error(event.value);
          } else if (isDifyDoneEvent(event)) {
            content.at(-1).status = 'Completed';
            doneFlag.value = true;
          }
        },
      },
    };
  });
}

export const DifyConversationPluginHelper = createPluginHelperByAggCreator(createAgg, agg => {
  delete aggMap[agg.__id];
});

export function useDifyConversationAgg(id: string) {
  let agg: ReturnType<typeof createAgg>;
  if (!aggMap[id]) {
    agg = createAgg(id);
    aggMap[id] = agg;
    DifyConversationPluginHelper.registerAgg(agg);
  }

  return aggMap[id].api;
}

export function destroy(conversationId) {
  aggMap[conversationId].api.destroy();
}

export const onRegisterEvent = onRegister.api;

function isDifyMessageEvent(event: DifyEvent): event is SSEDifyMessage {
  return event.type === 'dify.message';
}

function isDifyErrorEvent(event: DifyEvent): event is SSEDifyError {
  return event.type === 'dify.error';
}

function isDifyMetaEvent(event: DifyEvent): event is SSEDifyMeta {
  return event.type === 'dify.meta';
}

function isDifyDoneEvent(event: DifyEvent): event is SSEDifyDone {
  return event.type === 'dify.done';
}
