<template>
	<Dialog :is-show="isShowForm" :title="title" :width="640" class="dictionaryBox" @closeDialog="onCancel" @onSubmit="onSubmit">
		<div class="role-add_box">
			<el-form
			ref="formRef"
			:model="ruleForm"
			:rules="rules"
			label-position="right"
			:label-width="echartsFit(90)"
			class="demo-ruleForm"
			status-icon
		>
			<el-row>
				<el-col :span="18">
					<el-form-item label="角色名称:" prop="name">
						<el-input v-model="ruleForm.name" placeholder="请输入" /> 
					</el-form-item>
				</el-col>
				<el-col :span="18">
					<el-form-item label="角色类型:" prop="code">
						<el-select v-model="ruleForm.code" filterable clearable placeholder="请选择角色类型">
							<el-option v-for="el in roleType" :key="el.value" :label="el.label" :value="el.value" />
						</el-select>
					</el-form-item>
				</el-col>
			</el-row>
      <el-form-item label="描述:">
				<el-input v-model="ruleForm.description" :autosize="{ minRows: 3 }" type="textarea" placeholder="请输入" /> 
			</el-form-item>
    </el-form>
		</div>
	</Dialog>
</template>

<script setup>
import { ref, defineExpose, defineEmits } from 'vue';
import { cloneDeep } from 'lodash';
// import { addRole, editRole } from '@/api/manager/role';
import { echartsFit } from '@/utils';
const emits = defineEmits(['onRefresh']);

const roleType = ref([
	{ label: '管理员', value: 'admin' },
	{ label: '横向角色', value: 'transverse' },
	{ label: '纵向角色', value: 'portrait' }
	
]);

// 校验规则
const rules = ref({
	name: [
		{ required: true, message: '请输入角色名称', trigger: 'change' }
	],
	code: [
		{ required: true, message: '请选择角色类型', trigger: 'change' }
	]
});
const isShowForm = ref(false);
const formRef = ref();
const ruleForm = ref({
	name: '',
	code: '',
	description: '',
});

const onCancel = () => {
	isShowForm.value = false;
	ruleForm.value = {};
	formRef.value.resetFields();
};

const title = ref();
const openDailog = (row) => {
	title.value = '新增角色';
	if (row && row.id) {
		let param = cloneDeep(row);
		ruleForm.value.id = param.id;
		ruleForm.value.name = param.name;
		ruleForm.value.code = param.code;
		ruleForm.value.description = param.description;
		title.value = '编辑角色';
	}
	isShowForm.value = true;
};

const onSubmit = async () => {
	if (!formRef.value) return;
	await formRef.value.validate(async (valid, fields) => {
		if (valid) {
			const formData = cloneDeep(ruleForm.value);
			if (title.value === '编辑角色') {
				// const userInfo = await editRole(formData);
				// if (userInfo) {
				// 	emits('onRefresh');
				// }
				onCancel();
			} else {
				// const userInfo = await addRole(formData);
				// if (userInfo) {
				// 	emits('onRefresh');
				// }
				onCancel();
			}
		}
	});
};

defineExpose({
	openDailog,
});

</script>

<style lang="less" scoped>
.role-add_box {
	padding: 0 80px 21px 57px;
	overflow-y: auto;
}
</style>