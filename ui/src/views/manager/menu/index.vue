<template>
	<div class="content-manager">
	<div class="listWrap listWrap-head_height">
		<bz-table-header :searchConfig="searchConfig" :is-show-create="true" :type="'菜单'" @onSearch="handleSearch" @onCreate="addClick"></bz-table-header>
	</div>
	<div class="listWrap menuListWrap">
			<el-table 
				ref="tableRef" 
				:data="dataArray" 
				:stripe="false"
				row-key="id">
				<el-table-column label="菜单名称" prop="name" show-overflow-tooltip />
				<el-table-column label="类型" prop="type" show-overflow-tooltip >
					<template v-slot="scope">
						{{ scope.row.type === 1 ? '菜单' : '操作' }}
					</template>
				</el-table-column>
				<el-table-column label="上级菜单" prop="parentName" show-overflow-tooltip/>
				<el-table-column label="操作" align="center" :width="echartsFit(280)">
					<template #default="scope">
						<div style="display: flex;">
							<el-button type="primary" plain @click="drawerClick(scope.row)">
								<el-icon class="operate-margin-right"><View /></el-icon>详情
							</el-button>
							<el-button type="primary" plain class="operate-margin-left" @click="editClick(scope.row)">
								<el-icon class="operate-margin-right"><Edit /></el-icon>编辑
							</el-button>
							<el-button type="danger" plain class="operate-margin-left" @click="deleteClick(scope.row)">
								<el-icon class="operate-margin-right"><Delete /></el-icon>删除
							</el-button>
						</div>
					</template>
				</el-table-column>
				<template #empty>
					<Empty/>
				</template>
			</el-table>
		</div>
			
		<!-- 详情 -->
		<DetailDialog ref="detailDialogRef" />
		<!-- 新增/修改 -->
		<FormDialog ref="formDialogRef" @onRefresh="onRefresh" />
	</div>
</template>
<script setup>
import { ref, onMounted, reactive } from 'vue';
import { buildTree, echartsFit } from '@/utils'; // 树形结构被注释
import { ElMessage, ElMessageBox } from 'element-plus';
import { getAllList, removeMenu } from '@/api/manager/menu';
import DetailDialog from './DetailDialog.vue';
import FormDialog from './FormDialog.vue';
import { cloneDeep } from 'lodash';

const param = ref({});
// 表头筛选配置
const searchConfig = reactive([
	{ type: 'input', field: 'name', label: '菜单名称' }
]);
const menus = ref([]);
const dataArray = ref([]);
const getData = async () => {
	// 获取数据
	const res = await getAllList(param.value);
	const arr = [];
	const arr2 = [];
	if (res.data && res.data.length >= 0) {
		res.data.forEach(el => {
			arr2.push({ value: el.id, label: el.name, id: el.id, parent_id: el.parent_id });
			if (el.parent_id === '-1') {
				arr.push({ ...el, parentName: '根目录' });
			} else {
				arr.push({ ...el, parentName: res.data.find(e => e.id === el.parent_id)?.name });
			}
		});
		// 平面结构
		// menus.value = arr2;
		// dataArray.value = arr; 
		// 树形结构
		dataArray.value = buildTree(arr, 'id', 'parent_id'); 
		menus.value = buildTree(arr2, 'id', 'parent_id');
	}
	menus.value.push({ value: '-1', label: '根目录' });
};
// 条件搜索
const handleSearch = (e) => {
	param.value = {
		...e
	};
	getData();
};
const onRefresh = () => {
	getData();
};
const detailDialogRef = ref(); // 详情
// 查看详情
const drawerClick = (row) => {
	detailDialogRef.value.openDailog(row);
};
const formDialogRef = ref(); // 详情
// 新增
const addClick = () => {
	formDialogRef.value.openDailog(menus.value, '');
};
// 编辑
const editClick = (row) => {
	const formData = cloneDeep(row);
	formDialogRef.value.openDailog(menus.value, formData);
};
// 删除角色
const deleteClick = (row) => {
	ElMessageBox.prompt('请输入菜单名', '删除菜单', {
		type: 'warning',
		confirmButtonText: '确认',
		cancelButtonText: '取消',
		inputPlaceholder: '请输入菜单名',
		customClass: 'customMessageBox',
		inputValidator: (value) => value ? true : '请输入菜单名'
	}).then(async ({ value }) => {
		if (value === row.name) {
			const removeData = await removeMenu(row.id);
			if (removeData && removeData.code === 200) {
				onRefresh();
			}
		} else {
			ElMessage({
				type: 'warning',
				message: '菜单名输入错误，删除失败！',
			});
		}
	}).catch(() => {
		ElMessage({
			type: 'info',
			message: '取消删除！',
		});
	});
};
onMounted(() => {
	getData();
});
</script>
<style lang="less" scoped>
@import '@/assets/styles/table.less';
@import '@/assets/styles/list.less';
.listWrap-head_height {
	height: 32px;
}

.operate-margin-right {
	margin-right: 6px;
}

.operate-margin-left {
	margin-left: 6px;
}

.menuListWrap {
	height: calc(100% - 20px - 32px - 20px - 20px);
	overflow-y: auto;
	:deep(.el-table){
		height: 100%;
		// .el-table__body-wrapper {
		// 	max-height: 500px;
		// 	overflow-y: auto;
		// }
	}
}
</style>