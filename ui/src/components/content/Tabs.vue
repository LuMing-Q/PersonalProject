<!--
-	Tabs组件
-->
<template>
	<el-tabs v-model="activeName" type="border-card" class="demo-tabs" @tab-click="handleClick">
    <el-tab-pane v-for="(item, index) in props.tabList" :key="`${index}_${item.name}`" :label="item.label" :disabled="isDisabled" :name="item.name">
			<slot :name="item.name" />
		</el-tab-pane>
  </el-tabs>
</template>

<script setup>
import { ref, watch, defineProps, defineEmits } from 'vue';

const props = defineProps({
	active: { type: [String, Number], default: 0 },
	isDisabled: { type: Boolean, default: false },
	tabList: { type: Array, define: () => [] }
});

const activeName = ref(0);
activeName.value = props.active;

const emits = defineEmits(['handleClick']);

const handleClick = (e) => {
	emits('handleClick', e);
};

watch(() => props.active, (val) => {
	if (val) {
		activeName.value = val;
	}
}, { deep: true, immediate: true });
</script>

<style lang="less" scoped>
.demo-tabs{
	margin-top: 10px;
	height: 100%;
	// box-shadow: 0 0 16px -4px #99999933;
	:deep(.el-tabs__header){
		margin: 0;
		height: 36px;
		line-height: 36px;
		border: none;
		// background: #fff;
		background: #f5f5f5;
		box-shadow: 0 2px 4px 0 #0000001a;

		.el-tabs__item{
			padding: 0 34px;
			height: 34px;
			font-weight: 400;
			font-size: 14px;
			color: @text-color-Secondary;
		}
		.el-tabs__item.is-active{
			background: @brand-color-primary;
			border: none;
			color: #fff;
		}
	}
	:deep(.el-tabs__nav-wrap){
		padding: 0 8px;
		height: 100%;
	}
	:deep(.el-tabs__content){
		padding: 20px;
		height: calc(~"100% - 30px - 2 * 20px");
		overflow: auto;
		background: #fff;
		.el-tab-pane{
			height: 100%;
			overflow-y: auto;
		}
	}
}
</style>