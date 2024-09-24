<template>
	<Dialog :is-show="isShowForm" :title="title" :width="1260" class="dictionaryBox" @closeDialog="onCancel" @onSubmit="onSubmit">
		<div class="menu-add_box">
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
				<el-col :span="10">
					<el-form-item label="菜单名称:" prop="name">
						<el-input v-model="ruleForm.name" placeholder="请输入" /> 
					</el-form-item>
				</el-col>
				<el-col :span="4"></el-col>
				<el-col :span="10">
					<el-form-item label="类别:" prop="type">
						<div class="radio-box">
							<div v-for="option in options" :key="option.value" class="radio-box-btn">
								<input :id="option.value" v-model="ruleForm.type" type="radio" :value="option.value"/>
								<label :for="option.value">{{ option.text }}</label>
							</div>
						</div>
					</el-form-item>
				</el-col>
				<el-col :span="10">
					<el-form-item label="上级菜单:" prop="parent_id">
						<el-tree-select v-model="ruleForm['parent_id']" :data="menus" check-strictly :render-after-expand="false" style="flex: 1;"/>
					</el-form-item>
				</el-col>
				<el-col :span="4"></el-col>
				<el-col :span="10">
					<el-form-item label="排序:" prop="sort">
						<el-input v-model="ruleForm.sort" placeholder="请输入" /> 
					</el-form-item>
				</el-col>
				<el-col :span="10">
					<el-form-item label="页面地址:" prop="path">
						<el-input v-model="ruleForm.path" placeholder="请输入" /> 
					</el-form-item>
				</el-col>
				<el-col :span="4"></el-col>
				<el-col :span="10">
					<el-form-item label="菜单等级:">
						<el-input v-model="ruleForm.level" placeholder="请输入" /> 
					</el-form-item>
				</el-col>
				<el-col v-if="ruleForm['parent_id'] && ruleForm['parent_id'] === '-1'" :span="10">
					<el-form-item label="权限图标:" prop="icon">
						<el-input v-model="ruleForm.icon" placeholder="请输入" /> 
					</el-form-item>
				</el-col>
				<el-col v-if="ruleForm['parent_id'] && ruleForm['parent_id'] === '-1'" :span="4"></el-col>
				<el-col v-if="ruleForm.type === 0" :span="10">
					<el-form-item label="操作指令:" prop="icon">
						<el-input v-model="ruleForm.op_directive" placeholder="请输入" /> 
					</el-form-item>
				</el-col>
			</el-row>
    </el-form>
		</div>
	</Dialog>
</template>

<script setup>
import { ref, defineExpose, defineEmits } from 'vue';
import { cloneDeep } from 'lodash';
import { addMenu, editMenu } from '@/api/manager/menu';
import { echartsFit } from '@/utils';

const emits = defineEmits(['onRefresh']);

// 类型
const 																																	options = [
	{ text: '菜单', value: 1 },
	{ text: '操作', value: 0 },
];
		
// 校验规则
const rules = ref({
	name: [
		{ required: true, message: '请输入菜单名称', trigger: 'change' }
	],
	type: [
		{ required: true, message: '请选择菜单类型', trigger: 'change' }
	],
	parent_id: [
		{ required: true, message: '上级菜单不能为空', trigger: 'change' }
	],
	sort: [
		{ required: true, message: '排序不能为空', trigger: 'change' }
	],
	path: [
		{ required: true, message: '页面路径不能为空', trigger: 'change' }
	],
	icon: [
		{ required: true, message: '权限图标不能为空', trigger: 'change' }
	]
});
const isShowForm = ref(false);
const formRef = ref();
const ruleForm = ref({
	name: '',
	type: 1,
	available: 1,
});

const onCancel = () => {
	isShowForm.value = false;
	ruleForm.value = {};
	formRef.value.resetFields();
};

const menus = ref();
const title = ref();
const openDailog = (list, row) => {
	title.value = '新增菜单';
	if (list) {
		menus.value = list;
		ruleForm.value.type = 1;
	}
	if (row && row.id) {
		ruleForm.value = row;
		title.value = '编辑菜单';
	}
	isShowForm.value = true;
};

const onSubmit = async () => {
	if (!formRef.value) return;
	await formRef.value.validate(async (valid, fields) => {
		if (valid) {
			const formData = cloneDeep(ruleForm.value);
			formData.level = parseInt(formData.level);
			formData.sort = parseInt(formData.sort);
			if (title.value === '编辑菜单') {
				const menuInfo = await editMenu(formData);
				if (menuInfo) {
					emits('onRefresh');
					onCancel();
				}
			} else {
				const menuInfo = await addMenu(formData);
				if (menuInfo) {
					emits('onRefresh');
					onCancel();
				}
			}
		}
	});
};

defineExpose({
	openDailog,
});

</script>

<style lang="less" scoped>
.menu-add_box {
	padding: 0 80px 21px 57px;
	// height: 208px;
	overflow-y: auto;
}

.radio-box {
	display: flex; 
	height: 16px;
	line-height: 16px;
}

.radio-box-btn {
	margin-right: 20px;
	height: 16px;
	line-height: 16px;

	input {
		margin: 3px 7px 0px 5px;
		// height: 13px;
	}

	input[type=radio] {
		position: absolute;
		clip: rect(0, 0, 0, 0);
	}

	/* 未被选中的单选框样式 空心圆圈*/
	input[type=radio] + label::before {
		content: '';
		display: inline-block;
		vertical-align: middle;
		width: 12px;
		height: 12px;
		margin-right: 5px;
		box-sizing: border-box;
		border-radius: 50%;
		margin-bottom: 4px;
		border: 1px solid #bfcbd9;
	}

	/* 被选中的单选框样式 给空心圆圈中间加上背景色*/
	input[type=radio]:checked + label::before {
		background-color: #285FBB;
		border: 1px #285FBB solid;
		background-clip: content-box; /* 规定背景颜色的绘制区域 */
		padding: 2px;
	}

	/* 单选框文字的样式 */
	input[type=radio] + label {
		margin-right: 5px;
	}
}
</style>