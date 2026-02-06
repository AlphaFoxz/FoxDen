import { createMultiInstanceAgg, createBroadcastEvent, createPluginHelperByAggCreator } from 'vue-fn/domain';
import { type KnowledgeVo } from '@/api/system/knowledge/index';

const aggMap: Record<string, ReturnType<typeof createAgg>> = {};

function createAgg(_aggId: string) {
  return createMultiInstanceAgg(_aggId, () => {
    const tableLoading = ref(false);
    const aiBtnLoading = ref(false);
    const selectedAffairs = ref<KnowledgeVo[]>([]);
    const onAiMatch = createBroadcastEvent<{}>();
    return {
      states: { selectedAffairs, tableLoading, aiBtnLoading },
      commands: {
        setAiBtnLoading(v: boolean) {
          aiBtnLoading.value = v;
        },
        setMatchLoading(v: boolean) {
          tableLoading.value = v;
        },
        setSelectedAffairs(v: KnowledgeVo[]) {
          selectedAffairs.value = v;
        },
        initCheckbox(currentList: KnowledgeVo[]) {
          const list = currentList.map((item) => {
            for (const ent of selectedAffairs.value) {
              if (ent.id === item.id) {
                return true;
              }
            }
            return false;
          });
          return list;
        },
        filterAffairIds(ids: Array<number | string>) {
          const v = JSON.parse(JSON.stringify(selectedAffairs.value));
          selectedAffairs.value = v.filter((item: KnowledgeVo) => ids.includes(item.id));
        },
        toggleSelectedAffairs(selected: KnowledgeVo[], unselected: KnowledgeVo[]) {
          const list: KnowledgeVo[] = JSON.parse(JSON.stringify(selectedAffairs.value));
          for (const knowledge of selected) {
            let exsistIndex: any = '';
            for (const index in list) {
              if (list[index].id === knowledge.id) {
                exsistIndex = index;
                break;
              }
            }
            if (exsistIndex === '') {
              list.push(knowledge);
            }
          }

          for (const ent of unselected) {
            let exsistIndex: any = '';
            for (const index in list) {
              if (list[index].id === ent.id) {
                exsistIndex = index;
                break;
              }
            }
            if (exsistIndex !== '') {
              list.splice(exsistIndex, 1);
            }
          }
          selectedAffairs.value = list;
        },
        aiMatch() {
          onAiMatch.publish({});
        }
      },
      events: { onAiMatch }
    };
  });
}

export const MatchAffairPluginHelper = createPluginHelperByAggCreator(createAgg, (agg) => {
  delete aggMap[agg.__id];
});

export function useMatchAffairAgg(moduleName: string) {
  if (!aggMap[moduleName]) {
    const agg = createAgg(moduleName);
    aggMap[moduleName] = agg;
    MatchAffairPluginHelper.registerAgg(agg);
  }
  return aggMap[moduleName].api;
}
