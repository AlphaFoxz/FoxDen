<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave"> </transition>
    <el-card shadow="hover">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true" @submit.prevent>
            <el-form-item label="政策名称" prop="title">
              <el-input v-model="queryParams.title" placeholder="请输入政策名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
              <!-- <el-button icon="Refresh" @click="resetQuery">重置</el-button> -->
              <el-button
                v-if="aiMatch"
                type="warning"
                :loading="matchAffairAgg.states.aiBtnLoading.value"
                :disabled="!!aiMatchDisabled"
                @click="handleAiMatch"
              >
                AI匹配
              </el-button>
              <el-popover placement="bottom" :width="400" trigger="click">
                <template #reference>
                  <el-button icon="List">查看已选</el-button>
                </template>
                <el-tree-select
                  v-model="treeSelectValue"
                  :data="treeSelectOptions"
                  @change="handleTreeSelectChange"
                  class="el-custom-select"
                  popper-class="hidden"
                  multiple
                  :render-after-expand="false"
                  style="width: 372px"
                  placeholder="没有数据"
                />
              </el-popover>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
      <!-- <template #header>
            <el-row :gutter="10">
              <right-toolbar v-model:show-search="showSearch" @query-table="queryList"></right-toolbar>
            </el-row>
          </template> -->

      <el-table ref="affairsTableRef" border v-loading="loading" :data="affairsList" @select="handleSelect">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column v-if="false" label="角色编号" prop="roleId" width="120" />
        <el-table-column label="政策名称" prop="title" :show-overflow-tooltip="true" />
        <el-table-column label="AI政策解读" prop="affairOutline" :show-overflow-tooltip="true" />
        <el-table-column label="创建时间" prop="createTime" with="100">
          <template #default="scope">
            <span>{{ proxy.parseTime(new Date(scope.row.createTime)) }}</span>
          </template>
        </el-table-column>

        <!-- <el-table-column fixed="right" label="操作" width="180">
              <template #default="scope">
                <el-button type="primary" link @click="handleSendMessage(scope.row)">推送</el-button>
              </template>
            </el-table-column> -->
      </el-table>

      <pagination
        v-if="total > 0"
        v-model:total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="queryList"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { queryAffairPage, type KnowledgeVo } from '@/api/system/knowledge';
import { TableInstance } from 'element-plus';
import { useMatchAffairAgg } from './domain';

type ModuleId = 'EnterpriseInfo' | 'TrainingTask' | 'SendAffairTask';
interface Props {
  proxy: ComponentInternalInstance['proxy'];
  moduleId: ModuleId;
  aiMatch?: boolean;
  aiMatchDisabled?: boolean;
}
const props = defineProps<Props>();
const emits = defineEmits(['close']);

const matchAffairAgg = useMatchAffairAgg(props.moduleId);
const affairsTableRef = ref<TableInstance>();
const loading = ref(true);
const showSearch = ref(true);
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  title: ''
});
const affairsList = ref([]);
const total = ref(0);

async function handleQuery() {
  queryParams.pageNum = 1;
  queryList();
}
async function queryList() {
  loading.value = true;
  const res = await queryAffairPage(queryParams);
  affairsList.value = res.rows;
  total.value = res.total;
  loading.value = false;
  nextTick(handleInitCheckbox);
}
// function resetQuery() {}
onMounted(() => {
  queryList();
});
function handleInitCheckbox() {
  const opt = matchAffairAgg.commands.initCheckbox(affairsList.value);
  for (const index in opt) {
    if (opt[index]) {
      affairsTableRef.value!.toggleRowSelection(affairsList.value[index], undefined);
    }
  }
}
const treeSelectValue = ref([]);
const treeSelectOptions = ref([]);
watch(matchAffairAgg.states.selectedAffairs, (selectedAffairs: KnowledgeVo[]) => {
  treeSelectValue.value = selectedAffairs.map((map) => map.id);
  treeSelectOptions.value = selectedAffairs.map((aff) => {
    return { value: aff.id, label: aff.title };
  });
  const tmpSelectedEnterprises = affairsTableRef.value.getSelectionRows() as KnowledgeVo[];
  const shouldRenderUncheckedEnterprises = tmpSelectedEnterprises.filter((item) => {
    return !selectedAffairs.some((aff) => aff.id === item.id);
  });
  const shouldRenderCheckedEnterprises = affairsList.value
    .filter((aff) => !tmpSelectedEnterprises.some((item) => item.id === aff.id))
    .filter((item) => {
      return selectedAffairs.some((aff) => aff.id === item.id);
    });
  nextTick(() => {
    shouldRenderUncheckedEnterprises.forEach((aff) => {
      affairsTableRef.value!.toggleRowSelection(aff, undefined);
    });
    shouldRenderCheckedEnterprises.forEach((aff) => {
      affairsTableRef.value!.toggleRowSelection(aff, true);
    });
  });
});
function handleTreeSelectChange(ids: number[]) {
  console.debug('handleTreeSelectChange, current value: ', ids);
  matchAffairAgg.commands.filterAffairIds(ids);
}
function handleAiMatch() {
  console.debug('触发AI匹配政策');
  matchAffairAgg.commands.aiMatch();
}
function handleSelect(affairs: KnowledgeVo[]) {
  const selectedAffairs = affairs;
  const unselectedAffairs = affairsList.value.filter((item) => {
    for (const aff of affairs) {
      if (aff.id === item.id) {
        return false;
      }
    }
    return true;
  });
  console.debug('多选政策', selectedAffairs, unselectedAffairs);
  nextTick(() => {
    matchAffairAgg.commands.toggleSelectedAffairs(selectedAffairs, unselectedAffairs);
  });
}
</script>
