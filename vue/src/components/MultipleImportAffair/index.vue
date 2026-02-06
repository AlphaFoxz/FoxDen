<template>
  <SendAffair v-if="module === 'SendAffair'" :proxy="proxy" @uploadSuccess="$emit('uploadSuccess', $event)" @parseComplete="$emit('parseComplete', $event)" />
  <ImportAffair v-else-if="module === 'ImportAffair'" :proxy="proxy" />
</template>

<script setup lang="ts">
import { Module } from './domain';
import SendAffair from './SendAffair.vue';
import ImportAffair from './ImportAffair.vue';

type Props = {
  proxy: ComponentInternalInstance['proxy'];
  module: Module;
};
defineProps<Props>();
defineEmits(['uploadSuccess', 'parseComplete']);
</script>
