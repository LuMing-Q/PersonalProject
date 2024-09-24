<!-- 发起申请 -->
<template>
	<Dialog :is-show="isShowDialog" :title="contentData.title" width="1260" class="dictionaryBox" destroy-on-close :isTabs="true" @closeDialog="onCancel">
		<div class="tabs-box">
			<tabs :tabList="tabList" :isDisabled="isDetail" :active="tabList[tabNum]?.name">
				<template #OAExamineFile>
					<OA-examine-file :oaFlowType="oaFlowType" :detailData="detailData"/>
				</template>
				<template #reqInfo>
					<resource-apply></resource-apply>
				</template>
			</tabs>
		</div>

		<template v-slot:dialogFooter>
			<div class="footer">
				<el-button :loading="isLoading" @click="onCancel">取消</el-button>
				<el-button v-if="tabNum" :loading="isLoading" type="primary" @click="last">上一步</el-button>
				<el-button v-if="tabNum !== 1" :loading="isLoading" type="primary" @click="next">下一步</el-button>
				<el-button v-if="tabNum === 1 && !isDetail" :loading="isLoading" type="primary" plain  @click="onSubmit">暂存</el-button>
				<el-button v-if="tabNum === 1 && !isDetail" :loading="isLoading" type="primary" @click="onSubmit">提交</el-button>
			</div>
		</template>
	</Dialog>
</template>
  
<script setup>
import { ref, defineEmits } from 'vue';
import { getReceiveDetail, getInnerDetail } from '@/api/examine';
import OAExamineFile from './oaComponents/OAExamineFile.vue';
import ResourceApply from './oaComponents/ResourceApply.vue';

const emits = defineEmits(['onSubmit']);

const isShowDialog = ref(false);
// 详情需要可以点击切换 false
const isDetail = ref(false);

const contentData = ref({
	title: '发起申请',
});

const oaFlowType = ref('receive');

const detailData = ref({});

const tabList = [
	{ label: 'OA审批文件', name: 'OAExamineFile' },
	{ label: '资源申请单', name: 'reqInfo' },
];

const isLoading = ref(false);

const tabNum = ref(0); // tab选中的序号

const onCancel = () => {
	tabNum.value = 0;
	isShowDialog.value = false;
	detailData.value = {};
};

// 下一步
const next = async () => {
	tabNum.value += 1;
};

// 上一步
const last = () => {
	tabNum.value -= 1;
};

const getDetailData = async (id) => {
	let res;
	if (oaFlowType.value === 'receive') {
		res = await getReceiveDetail(id);
	} else {
		res = await getInnerDetail(id);
	}
	if (res && res.id) {
		if (oaFlowType.value !== 'receive') {
			res.zhengwen = [...res.realMainBodyShaex, ...res.realMainBodyDocShaex];
			detailData.value = res;
			detailData.value.inner = '内部流转';
		} else {
			res.zhengwen = [...res.mainBodyOfdShaex, ...res.mainBodyWpsShaex];
			detailData.value = res;
			detailData.value.receive = '收文';
		}
	}
	isShowDialog.value = true;
};

// 提交
const onSubmit = async () => {
	emits('onSubmit');
	onCancel();
};

const openDialog = (id = '', type = 'receive') => {
	oaFlowType.value = type;
	getDetailData(id);
};
  
defineExpose({
	openDialog,
});
</script>

<style scoped lang="less">
:deep(.el-dialog__body .content){
	padding: 0 !important;
}
.tabs-box{
	height: calc(~"100vh - 350px");
	.demo-tabs {
		:deep(.el-tabs__header){
			padding: 8px 20px 0;		
			
			.el-tabs__item {
				padding: 0 20px;
				background: #fff;
				margin-right: 8px;
			}

			.el-tabs__item.is-active {
				background: @brand-color-primary;
			}
		}
	}

	.el-tabs--border-card {
		border: none !important;
	}
}

.dialogWrap .el-dialog__header {
	border: none;
}
</style>
