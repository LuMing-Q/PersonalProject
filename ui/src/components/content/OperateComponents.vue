<!-- 操作列表 -->
<template>
	<div class="operate-wrap">
		<el-button v-for="(item, index) in operateObj.one" :key="`${item.cmd}_${index}`" :type="item.cmd === 'remove' ? 'danger' : 'primary'" plain @click="drawerClick(item.cmd)">
			<bz-icon :style="`margin-right: ${echartsFit(6)};`" :name="item.icon || 'icon-bianji'" />
			{{ item.label }}
		</el-button>

		<div v-if="operateObj.two && operateObj.two.length" class="popover-box">
			<el-button v-popover="popoverRef" v-click-outside="onClickOutside" type="primary" plain>
				<bz-icon name="icon-caozuo" />
			</el-button>

			<el-popover
				ref="popoverRef"
				popper-class="operate-popover-box"
				trigger="click"
				title=""
				virtual-triggering
				persistent
			>
				<div class="popover-content">
					<el-button v-for="(item, index) in operateObj.two" :key="`${item.cmd}_${index}`" :type="item.cmd === 'remove' ? 'danger' : 'primary'" link @click="drawerClick(item.cmd)">
						<bz-icon :style="`margin-right: ${echartsFit(6)};`" :name="item.icon || 'icon-bianji'" />
						{{ item.label }}
					</el-button>
				</div>
			</el-popover>
		</div>
		
	</div>
</template>

<script setup>
import { ref, unref, defineProps, watch, defineEmits } from 'vue';

import { echartsFit } from '@/utils';

const props = defineProps({
	showNum: { type: Number, default: 3 }, // 展示个数
	operateList: { type: Array, default: () => [] }, // 操作列表。lable：展示名；cmd：操作指令
});

const emits = defineEmits(['drawerClick']);

const operateObj = ref({});

const popoverRef = ref();

const onClickOutside = () => {
	popoverRef.value.delayHide();
};

const drawerClick = (cmd) => {
	emits('drawerClick', cmd);
	popoverRef.value?.hide();
};

watch(() => props.operateList, (val) => {
	if (val.length) {
		if (val.length > props.showNum) {
			if (props.showNum > 0) {
				operateObj.value = {
					one: val.slice(0, (props.showNum - 1)),
					two: val.slice((props.showNum - 1), val.length),
				};
			} else {
				operateObj.value = {
					one: [],
					two: val,
				};
			}
		} else {
			operateObj.value.one = val;
		}
	} else {
		operateObj.value = {};
	}
}, { deep: true, immediate: true });

</script>

<style lang="less" scoped>
.operate-wrap{
	display: flex;
	justify-content: flex-start;
	width: 100%;

	& > .el-button + :deep(.el-button){
		margin-left: 8px;
	}	

	:deep(.el-button){
		padding: 9px 15px;
		font-size: 14px;
	}

	.popover-box{
		margin-left: 8px;
	}
}

.popover-content{
	display: flex;
	flex-direction: column;
	justify-content: flex-start;

	:deep(.el-button){
		line-height: 28px;
	}
	& > .el-button + :deep(.el-button){
		margin-left: 0;
	}	
}
</style>

<style lang="less">
.el-popper.operate-popover-box{
	width: 80px !important;
	min-width: 80px !important;
	box-shadow: 0 0 9px 1px rgba(153, 153, 153, 0.2);
}
</style>
