<template>
	<Dialog :is-show="isShowForm" title="人员" :width="1260" :is-show-footer="false" class="resouce-detail-box" 
		:is-click-modal-close="true" @closeDialog="onCancel">
		<template #dialogHeader>
			<div class="dialog-header">
				<div class="dialog-header-left">
					<img v-if="baseInfo.isFileService == 1" src="@/assets/images/home/file.png">
					<img v-else src="@/assets/images/home/service.png">
					<div class="text-box">
						{{ baseInfo.serviceName }}
					</div> 
					<el-button v-if="baseInfo.btn == 1" type="success" plain>可用</el-button>
					<el-button v-if="baseInfo.btn == 2" type="info" plain>过期</el-button>
					<el-button v-if="baseInfo.btn == 3" type="warning" plain>授权中</el-button>
				</div>
				<div class="dialog-header-right">
					<div class="fixed-font-size">
						<bz-icon name="icon-faqi" class="iconSize16 icon-margin-right" />申请次数: {{ baseInfo.applyNum || 0 }}
					</div>
					<div class="fixed-font-size fixed-font-size-last">
						<bz-icon name="icon-xiangqing" class="iconSize16 icon-margin-right" />浏览次数: {{ baseInfo.viewNum || 0 }}
					</div>
					<el-button text @click="onCancel">
						<bz-icon name="icon-quxiao" class="iconSize16" />
					</el-button>
				</div>
			</div>
		</template>
		<div class="show-content-box">
			<detail-show :fields="baseFields" :data="baseInfo" />
			<!-- 共享类型 -->
			<div class="rosource-content-title-box">
				<bz-icon name="icon-gongxiangleixing" class="icon-box" />共享类型
			</div>
			<detail-show :fields="shareFields" :data="shareInfo" class="detail-show-padding-top" />
			<!-- 开放属性 -->
			<div class="rosource-content-title-box">
				<bz-icon name="icon-kaifangshuxing" class="icon-box" />开放属性
			</div>
			<detail-show :fields="openFields" :data="openInfo" class="detail-show-padding-top" />
			<!-- 信息项信息、服务/API调用说明 选项框 -->
			<div class="rosource-content-title-box1">
				<div class="content-box1" :style="isinfo ? 'border-bottom: 1px solid #285FBB; font-weight: bold;' : ''" @click="isinfo = true">
					<bz-icon name="icon-xinxixiang" class="icon-box" />信息项信息
				</div>
				<div class="content-box1" :style="!isinfo ? 'border-bottom: 1px solid #285FBB; font-weight: bold;' : ''" @click="isinfo = false">
					<bz-icon name="icon-shuoming" class="icon-box" />服务/API调用说明
				</div>
			</div>
			<!-- 信息项信息 -->
			<div v-if="isinfo" class="listWrap">
				<BzTable :isLocalData="true" :isShowOperate="false" :data="items" :isStripe="false">
					<el-table-column prop="name" label="信息项名称"/>
					<el-table-column prop="dataType" label="数据类型"  show-overflow-tooltip />
					<el-table-column prop="itemName" label="字段名称"  show-overflow-tooltip />
					<el-table-column prop="optionalValue" label="可选值" show-overflow-tooltip/>
					<el-table-column prop="remark" label="填写说明" show-overflow-tooltip/>
					<template #empty>
						<Empty :isLine="true"/>
					</template>
				</BzTable>
			</div>
			<!-- 服务/API调用说明 -->
			<div v-else class="listWrap listWrap-service">
				<div class="text-content-big-box">
					<div class="text-title-box">请求地址:</div>
					<div class="text-title-content">https://codesign.qq.com/app/design/418187507803909/board?team_id=30346</div>
				</div>
				<div class="text-content-big-box">
					<div class="text-title-box">调用方式:</div>
					<div class="text-title-content">https://codesign.qq.com/app/design/418187507803909/board?team_id=30346</div>
				</div>
				<div class="text-content-big-box">
					<div class="text-title-box">返回格式:</div>
					<div class="text-title-content">https://codesign.qq.com/app/design/418187507803909/board?team_id=30346</div>
				</div>
				<div class="text-content-big-box">
					<div class="text-title-box">请求示例:</div>
					<div class="text-title-content">https://codesign.qq.com/app/design/418187507803909/board?team_id=30346</div>
				</div>
				<div class="listWrap listWrap-param">
					<div class="param-title-box" @click="requestParam = !requestParam">
						请求参数 
						<el-icon v-if="requestParam" class="element-icon-box"><ArrowDown /></el-icon>
						<el-icon v-else class="element-icon-box"><ArrowUp /></el-icon>
					</div>
					<BzTable v-if="requestParam" :isLocalData="true" :isShowOperate="false" :data="dataArray" :isStripe="false">
						<el-table-column prop="name" label="参考代码"/>
						<el-table-column prop="type" label="参考名称"  show-overflow-tooltip />
						<el-table-column prop="field" label="参考类型"  show-overflow-tooltip />
						<el-table-column prop="option" label="是否必填" show-overflow-tooltip/>
						<el-table-column prop="description" label="参考位置" show-overflow-tooltip/>
						<el-table-column prop="description" label="操作符" show-overflow-tooltip/>
						<el-table-column prop="description" label="示例值" show-overflow-tooltip/>
						<el-table-column prop="description" label="默认值" show-overflow-tooltip/>
						<el-table-column prop="description" label="说明" show-overflow-tooltip/>
						<template #empty>
							<Empty :isLine="true"/>
						</template>
					</BzTable>
				</div>
				<div class="listWrap listWrap-param">
					<div class="param-title-box" @click="responseParam = !responseParam">
						返回参数
						<el-icon v-if="responseParam" class="element-icon-box"><ArrowDown /></el-icon>
						<el-icon v-else class="element-icon-box"><ArrowUp /></el-icon>
					</div>
					<BzTable  v-if="responseParam" :isLocalData="true" :isShowOperate="false" :data="dataArray" :isStripe="false">
						<el-table-column prop="english_name" label="英文名"/>
						<el-table-column prop="name" label="中文名"  show-overflow-tooltip />
						<el-table-column prop="type" label="数据类型"  show-overflow-tooltip />
						<el-table-column prop="description" label="说明" show-overflow-tooltip/>
						<template #empty>
							<Empty :isLine="true"/>
						</template>
					</BzTable>
				</div>
				<div v-if="jsonData" class="listWrap listWrap-param">
					<div class="param-title-box" @click="example = !example">
						返回示例
						<el-icon v-if="example" class="element-icon-box"><ArrowDown /></el-icon>
						<el-icon v-else class="element-icon-box"><ArrowUp /></el-icon>
					</div>
					<div v-if="example" class="json-show">
						<pre style="margin: 0;">{{ jsonFormat(jsonData) }}</pre>
					</div>
				</div>
			</div>
		</div>
		<template #dialogFooter>
			<div class="footer">
				<el-button>
					<bz-icon :name="baseInfo.collect ? 'icon-shoucang-yishoucang' : 'icon-shoucang'" class="iconSize18 icon-margin-right"></bz-icon>
					<span :style="baseInfo.collect ? 'color: #FFA200;' : ''">
						{{ baseInfo.collect ? '已收藏' : '收藏' }}
					</span>
				</el-button>
				<el-button v-if="baseInfo.isFileService == 1" type="primary">
					<bz-icon name="icon-xiazai" color="white" class="iconSize18 icon-margin-right"></bz-icon>下载
				</el-button>
			</div>
		</template>
	</Dialog>
</template>

<script setup>
import { ref, defineExpose } from 'vue';
import { getDetail } from '@/api/sharing/sharing';
import { day } from '@/utils';
import DetailShow from './resourceDetailComponent/DetailShow.vue';

const isShowForm = ref(false);
const isinfo = ref(true);
const requestParam = ref(true);
const responseParam = ref(true);
const example = ref(true);
// 基础信息
const baseInfo = ref({});
const baseFields = ref({
	serviceName: '资源名称',
	categoryName: '资源分类',
	serviceCode: '资源代码',
	resourceProvider: '资源提供方',
	resourceProviderCode: '资源提供方代码',
	serviceFormat: '资源格式',
	serviceRemark: '摘要',
});
// 共享类型
const shareInfo = ref({});
const shareFields = ref({
	sharType: '共享类型',
	openCondition: '开放条件',
	sharCondition: '共享条件'
});
// 开放属性
const openInfo = ref({});
const openFields = ref({
	isOpen: '是否向社会开放',
	openCondition: '开放条件',
	updatePeriod: '更新周期',
	publishTime: '发布日期',
	contactResourceCode: '关联资源代码'
});
const jsonData = ref('[{"ids":["1ded2f7167034a329be8807cf9bdffa1","47897bb3dff745eeb5b2f686c624683d"],"roleId":"8076b094abe44bb0adef2e35c472b4a9"}]');
// json格式化处理
const jsonFormat = (json) => {
	if (json !== '' && json !== undefined) {
	 	return JSON.stringify(JSON.parse(json), null, 2);
	} 
	return '-';
};
const items = ref();
const dataArray = ref();
const onCancel = () => {
	baseInfo.value = {};
	isShowForm.value = false;
};

const openDailog = async (id) => {
	let res = await getDetail(id);
	if (res) {
		baseInfo.value = res;
		shareInfo.value = {
			sharType: res.sharType === 1 ? '无条件共享' : res.sharType === 2 ? '有条件共享' : '不予共享',
			openCondition: res.openCondition,
			sharCondition: res.sharCondition
		};
		openInfo.value = {
			isOpen: res.isOpen === 1 ? '是' : '否',
			openCondition: res.openCondition,
			updatePeriod: res.updatePeriod,
			publishTime: day.getDay(res.publishTime, 'YYYY-MM-DD'),
			contactResourceCode: res.contactResourceCode
		};
		items.value = res.items;
	}
	isShowForm.value = true;
};

defineExpose({
	openDailog,
});

</script>

<style lang="less" scoped>
@import '@/assets/styles/detail.less';
@import '@/assets/styles/table.less';
.dialog-header {
	display: flex;
	justify-content: space-between;
	height: 16px;
	line-height: 16px;
	color: #30333A;
	font-size: 16px;
	font-weight: 350;
	width: calc(100% - 15px); 
	
	img {
		height: 16px;
		width: 36px;
		margin-right: 9px;
	}
}

.dialog-header-left {
	display: flex;
	align-items: center;
	width: calc(100% - 430px);
	
	.text-box {
		width: auto;
		max-width: calc(100% - 36px - 300px);
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
		margin-right: 15px;
	}
}

.dialog-header-right {
	display: flex;

	.fixed-font-size {
		width: 160px;
		color: #909399;
		display: flex;
		align-items: center;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;

		.icon-margin-right {
			margin-right: 9px;
		}
	}

	.fixed-font-size-last {
		margin: 0 55px;
		text-align: right;
	}
}

.show-content-box {
	max-height: calc(100vh - 320px);
	height: auto;
	overflow-y: auto;
}

.rosource-content-title-box {
	width: 100%;
	display: flex;
	align-items: center;
	height: 16px;
	line-height: 16px;
	font-weight: bold;
	color: #30333A;

	.icon-box {
		width: 16px !important;
		height: 16px !important;
		margin-right: 9px;
	}
}

.rosource-content-title-box1 {
	width: 272px;
	display: flex;
	justify-content: space-between;
	border-bottom: 1px solid #909399;

	.content-box1 {
		display: flex;
		align-items: center;
		height: 16px;
		line-height: 16px;
		padding-bottom: 9px;
		cursor: pointer;
		-webkit-user-select:none;
		-moz-user-select:none;
		-ms-user-select:none;
		user-select:none;
		
		.icon-box {
			width: 16px !important;
			height: 16px !important;
			margin-right: 7px;
		}
	}
}

.detail-show-padding-top {
	margin-top: 32px;
}

.listWrap {
	margin-top: 24px;
	padding: 0;
	width: 100%;

	.bzTable :deep(.el-table__body-wrapper){
		max-height: 265px;
		overflow-y: auto;
	}
}

.listWrap-service {
	padding: 28px;
	height: auto;
	width: calc(100% - 56px);
	background: #F9FAFC;

	.text-content-big-box {
		width: 100%;
		display: flex;
		margin-bottom: 24px;

		.text-title-box {
			width: 86px;
			color: #5A5A5A;
		}

		.text-title-content {
			flex: 1;
			color: #30333A
		}
	}
}

.listWrap-param {
	padding: 16px 20px;
	width: calc(100% - 40px);

	.param-title-box {
		display: flex;
		align-items: center;
		height: 14px;
		line-height: 14px;
		cursor: pointer;
		-webkit-user-select:none;
		-moz-user-select:none;
		-ms-user-select:none;
		user-select:none;
	}

	.element-icon-box {
		margin-left: 12px;
	}

	.bzTable {
		margin-top: 20px;
	}

	.json-show {
		background-color: white; 
		border: 1px solid #DCDFE6; 
		padding: 10px; 
		margin-top: 20px;
		font-size: 18px;
		height: auto;
		max-height: 200px;
		overflow-y: auto;
	}
}
</style>