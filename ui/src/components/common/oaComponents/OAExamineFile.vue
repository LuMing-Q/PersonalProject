<!-- OA审批文件 -->
<template>
	<div class="examine-file-Box">
		<el-row v-if="!isEmpty(detailData)" class="detailWrap">
			<el-col v-for="(item, index) in showFields" :key="`${item.fields}_${index}`" 
				:span="item.span || 8" :class="item.type === 'file' ? 'file-col-bottom-margin' : ''">
				<div class="labelWrap">{{ item.name }}</div>

				<div v-if="item.type === 'time'" class="valueWrap">{{ day.getDay(detailData[item.fields]) }}</div>

				<div v-else-if="item.type === 'join'" class="valueWrap">{{ detailData[item.fields] && detailData[item.fields].join(',') || '-' }}</div>

				<div v-else-if="item.type === 'object'" class="valueWrap">{{ detailData[item.fields]?.[item.levelFieldsArr] || '-' }}{{ item.unit || '' }}</div>

				<div v-else-if="item.type === 'array'" class="valueWrap">{{ detailData[item.fields] && detailData[item.fields][0][item.levelFieldsArr] || '-' }}</div>

				<div v-else-if="item.type === 'img'" class="valueWrap">
					<template v-if="item.fields && detailData[item.fields]">
						<img :src="detailData[item.fields].url" class="typeimg"/>
						<span style="margin-left: 16px;">{{ detailData[item.fields].name }}</span>
					</template>
					<span v-else>-</span>
				</div>

				<div v-else-if="item.type === 'file'" class="valueWrap click-file-box">
					<el-row>
						<el-col v-for="(file, index) in detailData[item.fields]" :key="index" :span="8" class="file-bottom-margin" @click="viewsFile(file.objectName)">
							<bz-icon :name="item.fields === 'zhengwen' ? 'icon-zhengwen' : 'icon-fujian'" class="file-icon" />
							<div style="color: #285FBB;">{{ fileNameSlice(file.filename, 20, 13) }}</div></el-col>
					</el-row>
				</div>
				
				<div v-else class="valueWrap">{{ detailData[item.fields] || '-' }}{{ item.unit || '' }}</div>
			</el-col>
		</el-row>

		<div class="sign-box">
			<div class="sign-list-first-box">
				<div v-for="item in signDataList" :key="item" class="sign-list-box">
					<sign-card :data="item" />
				</div>
			</div>
			<div class="sign-list-second-box" style="overflow-y: auto;">
				<sign-card :data="signData" class="sign-card-heigth" />
			</div>
		</div>

		<div v-for="(item, index) in detailData.accessoryShaex || detailData.endFile" :key="index" class="apply-file-box">
			<div class="file-upload-file">
				<div class="pre">
					<img src="@/assets/images/file_pre.png">
				</div>
				<div class="file-title-box" style="cursor: default">
					办结附件:
				</div>
				<div class="file-name-box click-file-box" @click="viewsFile(item.objectName)">
					<bz-icon :name="'icon-wenjian'" class="file-icon" />
					<div class="file-name">{{ fileNameSlice(item.filename) }}</div>
				</div>
			</div>
		</div>		
	</div>
</template>

<script setup>
import { ref, defineProps, onMounted } from 'vue';
import { isEmpty } from 'lodash';
import { day } from '@/utils';
import SignCard from './SignCard.vue';

const props = defineProps({
	oaFlowType: { type: String, default: '' },
	detailData: { type: Object, default: () => {} },
});

// 文字省略
const fileNameSlice = (param, total = 60, pre = 50) => {
	if (param?.length && param.length > total) {
		return param.slice(0, pre) + '...' + param.slice(-7);
	} 
	return param;
};

// 展示配置
const receiveShowFields = ref([
	{ fields: 'title', name: '文件标题', span: 24 },
	{ fields: 'receive', name: '文件类型' }, // 收文
	{ fields: 'sourceDept', name: '来文单位' },
	{ fields: 'docno', name: '公文字号' },
	{ fields: 'docType', name: '公文类型' },
	{ fields: 'docSource', name: '公文来源' },
	{ fields: 'urgency', name: '紧急程度' },
	{ fields: 'doingCode', name: '收文号' },
	{ fields: 'publicWay', name: '公开类型' },
	{ fields: 'contentType', name: '收文处室' },
	{ fields: 'receviceDate', name: '收文日期', type: 'time' },
	{ fields: 'writtenDate', name: '成文日期', type: 'time' },
	{ fields: 'wjfs', name: '文件份数' },
	{ fields: 'wjys', name: '文件页数' },
	{ fields: 'fjgs', name: '附件个数' },
	{ fields: 'zhengwen', name: '正文', span: 24, type: 'file' },
	{ fields: 'accessoryShaex', name: '附件', span: 24, type: 'file' },
]);

// 内部流转
const innerShowFields = ref([
	{ fields: 'title', name: '文件标题', span: 24 },
	{ fields: 'inner', name: '文件类型' }, 
	{ fields: 'zhengwen', name: '正文', span: 24, type: 'file' },
	{ fields: 'accessoryShaex', name: '附件', span: 24, type: 'file' },
]);

const showFields = ref([]);

const signDataList = ref([]);

const signData = ref();

const viewsFile = (objectName = '') => {
	window.open(`/file_preview?fileName=${objectName}`);
};

const assignment = (type) => {
	signDataList.value = [];
	signData.value = {};
	if (!type) return;
	if (type === 'receive') {
		showFields.value = receiveShowFields.value;
		signDataList.value.push({
			title: '厅领导批示',
			list: props.detailData.leaderassign
		});
		signDataList.value.push({
			title: '办公室签批',
			list: props.detailData.officeDispose
		});
		signDataList.value.push({
			title: '业务处室领导批示',
			list: props.detailData.doingLeaderName
		});
		signDataList.value.push({
			title: '办理结果',
			list: props.detailData.disposeResultName
		});
		signData.value = {
			title: '传阅签字',
			list: props.detailData.seeName
		};
	} else {
		showFields.value = innerShowFields.value;
		signDataList.value.push({
			title: '处长批示',
			list: props.detailData.csLeaderOpinion
		});
		signDataList.value.push({
			title: '分发人意见',
			list: props.detailData.dispenseOpinion
		});
		signDataList.value.push({
			title: '办理结果',
			list: props.detailData.disposeResult
		});
		signData.value = {
			title: '反馈意见',
			list: props.detailData.feedbackOpinion
		};
	}
};

onMounted(async () => {
	assignment(props.oaFlowType);
});
</script>

<style lang="less" scoped>
@import '@/assets/styles/detail.less';

.examine-file-Box {
	padding-right: 5px;
	height: 100%;
	overflow-y: auto;
	width: calc(100% - 5px);
	.detailWrap {
		.el-col-8{
			padding: 0 !important;
		}

		.file-col-bottom-margin {
			margin-bottom: 7px;
		}
	}
}

.click-file-box {
	cursor: pointer;
	-webkit-user-select: none;
	-moz-user-select: none;
	-ms-user-select: none;
	user-select: none;

	.file-bottom-margin {
		margin-bottom: 5px;
	}
}

.file-list{
	display: flex;
	justify-content: flex-start;
	margin: 25px 0;
	.label{
		flex-shrink: 0;
		width: 60px;
		text-align: right;
	}
	.value{
		margin-left: 20px;
		cursor: pointer;
		-webkit-user-select:none;
		-moz-user-select:none;
		-ms-user-select:none;
		user-select:none;
		color: #1790FF;
	}
}

.sign-box{
	display: flex;
	justify-content: space-between;

	.sign-list-first-box {
		width: 640px;
		margin-right: 20px;
		height: 100%;
	}

	.sign-list-second-box {
		width: calc(100% - 640px - 20px - 5px);
		overflow-y: auto;
		padding-right: 5px;
		.sign-card-heigth {
			height: calc(100% - 2px - 25px);
		}
	}

	.sign-list-box + .sign-list-box{
		margin-top: 20px;
		flex-wrap: wrap
	}
}

.apply-file-box {
	width: 100%;
	display: flex;
	flex-direction: column;

	.file-upload-file {
			width: 100%;
			position: relative;
			z-index: 1;
			border: 1px solid #DEEBFE;
			height: 92px;
			border-radius: 4px;
			margin-top: 18px;
			background-color: #F9FBFF;
			display: flex;
			align-items: center;

			.pre img{
				height: 92px;
				width: 27px;
				vertical-align: bottom;
			}

			.file-title-box {
				width: 72px;
				font-weight: bold;
				margin-right: 20px;
				margin-left: 22px;
			}

			.file-name-box {
				padding-right: 14px;
				height: 24px;
				line-height: 24px;
				width: calc(100% - 17px - 28px);
				font-size: 14px;
				color: #285FBB;
				display: flex;
				align-items: center;
				// justify-content: space-between;
				-webkit-user-select:none;
				-moz-user-select:none;
				-ms-user-select:none;
				user-select:none;

				.file-icon {
					width: 26px !important;
					height: 28px !important;
					fill: black;
					margin-right: 12px;
				}

				.file-name {
					text-align: left;
					text-wrap: nowrap;
					overflow: hidden;
				}
			}
		}
}

.file-icon {
	width: 22px !important;
	height: 24px !important;
	fill: black;
	margin-right: 12px;
}
</style>
