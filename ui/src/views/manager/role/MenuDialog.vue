<template>
	<Dialog :is-show="isShowForm" :title="title" :width="630" class="dictionaryBox" @closeDialog="onCancel" @onSubmit="onSubmit">
		<div class="menu-dialog_box">
			<el-tree ref="tree" :data="dataSource" :show-checkbox="true" :check-strictly="true" node-key="id"
							:default-expanded-keys="defaultChecked" :default-checked-keys="defaultChecked" :props="defaultProps"
							style="width: 100%;border: solid 1px #D7D7DD;border-radius: 3px;" @check-change="getCheckedKeys" />
		</div>
	</Dialog>
</template>

<script setup>
import { defineEmits, defineExpose, ref, watch } from 'vue';
import { buildTree } from '@/utils';
import { addRoleMenu, getAllList } from '@/api/manager/menu';
import { getUserMenuList } from '@/api/platform/userInfo';

const roleId = ref();
const roleCode = ref();
const isShowForm = ref(false);
const emits = defineEmits(['onRefresh']);

const title = ref();
const openDailog = (id, code) => {
	roleId.value = id;
	roleCode.value = code;
	title.value = '菜单权限';
	isShowForm.value = true;
};

// 树形控件
const tree = ref({});
const dataSource = ref([]);
const menus = ref([]);
const defaultChecked = ref(); // 默认勾选的节点
const selectedNodeKeys = ref([]); // 要提交的选中节点
const selected = ref([]);
// 要提交的选中节点
const defaultProps = {
	children: 'children',
	label: 'name',
	value: 'id'
};
// 接受选中的节点数据
const getCheckedKeys = (row, show) => {
	if (!show) {
		// 取消勾选
		if (row.children) {
			row.children.map(item => {
				tree.value.setChecked(item.id, false);
			});
		}
	} else {
		if (row) {
			const findParent = (parent_id = -1) => {
				const result = menus.value.find(r => r.id === parent_id);
				if (result) {
					tree.value.setChecked(result.id, true);
					if (result.parent_id != 0) findParent(result.parent_id);
				}
			};
			// 向上查找
			if (row.parent_id != 0) findParent(row.parent_id);
		}
	}
	const res = tree.value.getCheckedNodes(false, true);
	selectedNodeKeys.value = res.map(r => r.id);
	selected.value = res.map(r => r.id);
};
 
// 获取权限列表
const getMandate = async () => {
	let res = await getAllList();
	let menuList = res;
	if (roleId.value) {
		// 默认选中菜单
		const checkedList = await getUserMenuList(roleId.value);
		// 将获取到的菜单ID列表赋值给默认选中的菜单ID列表
		defaultChecked.value = checkedList.map(r => r.id);
	}
	menus.value = menuList;
	if (roleCode.value == 'admin') {
		dataSource.value = buildTree(menuList, 'id', 'parent_id');
	} else {
		// 过滤出当前角色的菜单
		dataSource.value = buildTree(menuList, 'id', 'parent_id');
		dataSource.value = dataSource.value.filter(r => r.name !== '后台管理');
	}
};

const onCancel = () => {
	roleId.value = '';
	selected.value = [];
	selectedNodeKeys.value = [];
	defaultChecked.value = [];
	isShowForm.value = false;
};

const onSubmit = async () => {
	let formData = {
		roleId: roleId.value,
		ids: selected.value
	};
	const res = await addRoleMenu(formData);
	if (res) {
		isShowForm.value = false;
		emits('onRefresh');
	}
};

defineExpose({
	openDailog,
});


watch(() => isShowForm.value, (val) => {
	if (val) {
		getMandate();
	}
}, { deep: true, immediate: true });
</script>

<style lang="less" scoped>
.menu-dialog_box {
	padding: 0;
	height: 470px;
	overflow-y: auto;

	:deep(.el-tree) {
		border: none !important;
	}
}
</style>