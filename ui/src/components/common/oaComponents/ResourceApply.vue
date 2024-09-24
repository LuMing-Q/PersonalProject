<!-- 资源申请单 -->
<template>
	<el-form ref="formRef" :model="ruleForm" :rules="rules" label-position="right" :label-width="echartsFit(125)" class="demo-ruleForm"
		status-icon @submit.enter.prevent>
		<el-row style="display: flex; justify-content: space-between;">
			<template v-for="(item, index) in formConfig" :key="`${item.field}_${index}`">
				<el-col :span="item.span || 10">
					<el-form-item :label="item.label" :prop="item.field">
						<template v-if="item.field === 'num'" #label>
							<!-- <form-label :labelConfig="item" /> -->
							<div class="form-lable">
								{{ item.label }}
							</div>
						</template>

						<!-- 开关 -->
						<el-switch v-if="item.type === 'switch'" v-model="ruleForm[item.field]"/>

						<!-- 下拉选择 -->
						<el-select v-else-if="item.type === 'select'" v-model="ruleForm[item.field]" filterable clearable :placeholder="`请选择${item.label}`">
							<el-option v-for="el in item.options" :key="el.value || el.id" :label="`${el.label || el.name}`" :value="el.value || el.id" />
						</el-select>
						
						<el-input v-else-if="item.type === 'textarea'" v-model="ruleForm[item.field]" :autosize="{ minRows: 3 }" maxlength="1000" :type="item.type" placeholder="请输入" />
						
						<div v-else-if="item.type === 'way'">
							<div class="status">
								<div class="ring"></div>
								<span class="state" style="color: #285FBB;">根据OA审批文件申请</span>
							</div>
						</div>

						<el-tree v-else-if="item.type === 'tree'" ref="tree" :data="dataSource" :show-checkbox="true" :check-strictly="true" node-key="id"
							:default-expanded-keys="defaultChecked" :default-checked-keys="defaultChecked" :props="defaultProps"
							style="width: 100%;border: solid 1px #D7D7DD;border-radius: 3px;" @check-change="getCheckedKeys" />

						<el-tree v-else-if="item.type === 'treeChech'" ref="tree" :data="dataSource" :show-checkbox="true" :check-strictly="true" node-key="id"
							:default-expanded-keys="defaultChecked" :default-checked-keys="defaultChecked" :props="defaultProps"
							style="width: 100%;border: solid 1px #D7D7DD;border-radius: 3px;" @check-change="getCheckedKeys" />

						<el-button v-else-if="item.type === 'button'" :loading="isLoading" type="primary" plain  @click="add">
							<el-icon><Plus /></el-icon>添加更多资源
						</el-button>

						<el-date-picker v-else-if="item.type === 'date'" v-model="ruleForm[item.field]" type="date" size="default" style="flex: 1;"/>

						<el-input v-else v-model.trim="ruleForm[item.field]" maxlength="60" :type="item.type || 'text'" placeholder="请输入">
						</el-input>
					</el-form-item>
				</el-col>
				<el-col v-if="item.field === 'persone'" :span="3">
					<div style="text-align: left; color: #285FBB;">( 注: 系统应用和经办人请至少填写一项 )</div>
				</el-col>
			</template>
		</el-row>
	</el-form>
	<el-collapse v-model="activeName" accordion class="collapse-box">
		<el-collapse-item v-for="(item,index) in formData" :key="index" :title="item.name" :name="++index">
				<div class="content">{{ index }}</div>
		</el-collapse-item>
	</el-collapse>
</template>
<script setup>
import { ref, defineEmits } from 'vue';
import { echartsFit } from '@/utils';

const emits = defineEmits(['onRefresh']);
const serverList = ref([]);

// label展示及提示信息配置
const formConfig = ref([
	{ label: '名称', type: 'way', field: 'name', defaultVal: '' },
	{ label: 'OA审批文件编号', field: 'num', options: serverList.value, defaultVal: '' },
	{ label: '申请单位', type: 'select', field: 'dan', options: serverList.value, defaultVal: '' },
	{ label: '系统应用', type: 'select', field: 'system', defaultVal: '' },
	{ label: '经办人', type: 'select', field: 'persone', defaultVal: '' },
	{ label: '经办人电话', field: 'phone', defaultVal: '' },
	{ label: '经办人电子邮箱', field: 'email', defaultVal: '' },
	{ label: '需求描述', type: 'textarea', field: 'decribe', defaultVal: '', span: 24 },
	{ label: '用途', type: 'textarea', field: 'use', defaultVal: '', span: 24 },
	{ label: '资源申请', type: 'button', field: 'resource', defaultVal: '' },
	{ label: '使用截至日期', type: 'date', field: 'date', defaultVal: '' },
]);

const formRef = ref();
const ruleForm = ref({
	name: '',
});

// 校验规则
const rules = ref({
	name: [
		{ required: true, message: '请输入备份名称', trigger: ['blur', 'change'] }
	],
	num: [
		{ required: true, message: '请选择所属项目', trigger: ['blur', 'change'] }
	],
	server_id: [
		{ required: true, message: '请选择云主机', trigger: ['blur', 'change'] }
	],
});

const activeName = ref(1);
const formData = ref([
	{
		name: '关于地理国情监测的信息关于地理国情监测的信息'
	}, {
		name: '2024农村自建房规划2024农村自建房规划'
	}, {
		name: '2023耕地监测'
	},
]);
</script>
<style lang="less" scoped>
.form-lable {
	line-height: 14px !important;
	text-align: right !important;
}
.demo-ruleForm { 
	padding-right: 10px;
}

.collapse-box {
	border: none;
	margin-right: 10px;

	:deep(.el-collapse-item) {
		margin-bottom: 12px;
		position: relative;
	}
	
	:deep(.el-collapse-item__content){
		border: none;
		padding: 0 20px 12px;
		background-color: #F9FAFC;
	}

	:deep(.el-collapse-item__header){
		position: relative;
		padding-left: 31px;
		color: #30333A;
		font-weight: bold;
		background-color: #F9FAFC;
		text-align: left;
		border-radius: 4px;
		border: none;
		height: 54px;
		line-height: 54px;

		.el-collapse-item__arrow{
			position: absolute;
			left: 13px;
			top: 20px;
		}
	}

	:deep(.el-collapse-item__header.is-active) {
		border: none;
		background-color: #F9FAFC	;
	}
}

.content {
	background-color: aquamarine;
	height: 30px;
}
</style>