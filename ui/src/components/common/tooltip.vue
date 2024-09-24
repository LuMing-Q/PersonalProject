<template>
      <span>
        {{name}}
      </span>
      <el-tooltip	placement="top-start"	>
        <template #content> {{content}}</template>
        <el-icon class="warning"><Warning /></el-icon>
      </el-tooltip>
</template>
<script name='tooltip' setup>
defineProps({
	name: { type: String, default: '' },
	content: { type: String, default: '' },
});
</script>
<style lang="less">
.warning{
  margin-top: 8px !important;
}
</style>
