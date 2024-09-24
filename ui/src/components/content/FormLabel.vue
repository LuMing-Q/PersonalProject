<template>
	<div class="formLabel">
		<div class="label">{{ props.labelConfig.label }}</div>
		<div class="tip">
			<el-tooltip v-if="props.labelConfig.tipText" popper-class="formLabelTooltip" effect="dark" :content="props.labelConfig.tipText" placement="right">
				<bz-icon name="icon-zhushi" :size="14" color="#3E7EE8" class="iconSize14" />
			</el-tooltip>
		</div>
	</div>
</template>

<script name="FormLabel" setup>
import { defineProps } from 'vue';

const props = defineProps({
	labelConfig: { type: Object, default: () => ({ label: '标题', tipText: '提示内容' }) }, // label展示字段及提示内容
});

</script>

<style lang="less" scoped>
.formLabel{
	width: 100%;
	display: flex;
	justify-content: space-between;

	& > div{
		flex-shrink: 0;
	}
	.label{
		width: calc(100% - 16px);
	}
	.tip{
		width: 16px;
	}
}
</style>

<style lang="less">
	.el-popper.is-dark.formLabelTooltip{
		max-width: 200px;
	}
</style>