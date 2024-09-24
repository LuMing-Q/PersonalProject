<template>
	<div class="content-manager">
		<div class="listWrap listWrap-head_height" style="margin:0">
			<bz-table-header :searchConfig="searchConfig" :is-show-create="true" :type="'角色'" @onSearch="handleSearch" @onCreate="addClick"></bz-table-header>
		</div>
		<div class="listWrap" style="flex: 1;padding-bottom: 0;">
			<el-table
				ref="tableRef" 
				:data="dataArray" 
				:stripe="false">
				<el-table-column label="角色名称" prop="name" show-overflow-tooltip />
				<el-table-column label="描述" prop="description" show-overflow-tooltip />
				<el-table-column label="创建时间" prop="createOn" :formatter="(row) => (day.getDay(row.createOn))"/>
				<el-table-column label="操作" align="center" :width="echartsFit(400)">
					<template #default="scope">
						<div style="display: flex;">
							<el-button type="primary" plain @click="drawerClick(scope.row)">
								<el-icon class="operate-margin-right"><View /></el-icon>详情
							</el-button>
							<el-button type="primary" plain class="operate-margin-left" @click="editClick(scope.row)">
								<el-icon class="operate-margin-right"><Edit /></el-icon>编辑
							</el-button>
							<el-button type="primary" plain class="operate-margin-left" @click="menuClick(scope.row)">
								<bz-icon :name="'icon-caidanquanxian'" :size="echartsFit(16)" class="iconSize16 operate-margin-right" />菜单权限
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
			<Pagination :total="total" :pageInfo="page"  @returnPageInfo="getPagiation" />
		</div>
		<!-- 详情 -->
		<DetailDialog ref="detailDialogRef" />
		<!-- 新增 -->
		<AddDialog ref="addDialogRef" @onRefresh="onRefresh" />
		<!-- 新增 -->
		<MenuDialog ref="menuDialogRef" @onRefresh="onRefresh" />
	</div>
</template>
<script setup>
import { ref, onMounted, reactive } from 'vue';
import { day, echartsFit } from '@/utils';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getRoleList, removeRole } from '@/api/manager/role';
import DetailDialog from './DetailDialog.vue';
import AddDialog from './AddDialog.vue';
import MenuDialog from './MenuDialog.vue';

const total = ref();
const page = ref();
const param = ref({
	page: 1,
	size: 10
});
// table 回到总数和当前页码
const getTotal = (res) => {
	total.value = res.total;
	page.value = res.page;
};
// 表头筛选配置
const searchConfig = reactive([
	{ type: 'input', field: 'name', label: '角色名称' }
]);
const dataArray = ref([]);
const getData = async () => {
	// 获取数据
	const res = await getRoleList(param.value);
	if (res && res.data) {
		dataArray.value = res.data;
		total.value = res.total;
	}
};
// 条件搜索
const handleSearch = (e) => {
	param.value = {
		page: 1,
		size: 10,
		...e
	};
	getData();
};
const onRefresh = () => {
	param.value = {
		page: 1,
		size: 10
	};
	getData();
};
// 分页
const getPagiation = (res) => {
	param.value.page = res.page;
	param.value.size = res.size;
	getData();
};
const detailDialogRef = ref(); // 详情
// 查看详情
const drawerClick = (row) => {
	detailDialogRef.value.openDailog(row);
};
const addDialogRef = ref(); // 详情
// 新增
const addClick = () => {
	addDialogRef.value.openDailog();
};
// 新增
const editClick = (row) => {
	addDialogRef.value.openDailog(row);
};
const menuDialogRef = ref(); // 详情
// 新增
const menuClick = (row) => {
	menuDialogRef.value.openDailog(row.id);
};
// 删除角色
const deleteClick = (row) => {
	ElMessageBox.prompt('请输入角色名', '删除角色', {
		type: 'warning',
		confirmButtonText: '确认',
		cancelButtonText: '取消',
		inputPlaceholder: '请输入角色名',
		customClass: 'customMessageBox',
		inputValidator: (value) => value ? true : '请输入角色名'
	}).then(async ({ value }) => {
		if (value === row.name) {
			const removeData = await removeRole(row.id);
			if (removeData && removeData.code === 200) {
				onRefresh();
			}
		} else {
			ElMessage({
				type: 'warning',
				message: '角色名输入错误，删除失败！',
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
</style>