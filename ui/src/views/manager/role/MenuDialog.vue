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
import { ref, defineExpose, defineEmits, watch } from 'vue';
import { buildTree, echartsFit } from '@/utils';
import { getAllList } from '@/api/manager/menu';
// import { listMenu, grantMenu } from '@/api/manager/role';
const roleId = ref();
const isShowForm = ref(false);
const emits = defineEmits(['onRefresh']);

const onCancel = () => {
	isShowForm.value = false;
};

const title = ref();
const openDailog = (id) => {
	roleId.value = id;
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
			const findParent = (parentCode = -1) => {
				const result = menus.value.find(r => r.code === parentCode);
				if (result) {
					tree.value.setChecked(result.id, true);
					if (result.parentId != 0) findParent(result.parentId);
				}
			};
			// 向上查找
			if (row.parentCode != 0) findParent(row.parentCode);
		}
	}
	const res = tree.value.getCheckedNodes(false, true);
	selectedNodeKeys.value = res.map(r => r.id);
	selected.value = res.map(r => r.id);
};
 
// 获取权限列表
const getMandate = async () => {
	let res = await getAllList();
	// 管理员查询所有菜单 getAllMenuList
	let menuList = res;
	if (roleId.value) {
		// 默认选中菜单
		// const checkedList = await listMenu(roleId.value);
		// defaultChecked.value = checkedList;
	}
	menus.value = menuList;
	dataSource.value = buildTree(menuList, 'code', 'parentCode');
};

const onSubmit = async () => {
	let formData = {
		roleId: roleId.value,
		ids: selected.value
	};
	// const res = await grantMenu(formData);
	// if (res) {
	// 	isShowForm.value = false;
	// 	emits('onRefresh');
	// }
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
	padding: 0 0 9px 43px;
	height: 470px;
	overflow-y: auto;

	:deep(.el-tree) {
		border: none !important;
		.el-tree-node.is-expanded > .el-tree-node__children .el-tree-node__content{
			padding-left: 18px !important;
		}
	}
}
</style>