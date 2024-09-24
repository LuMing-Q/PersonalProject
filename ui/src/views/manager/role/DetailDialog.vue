<template>
	<Dialog :is-show="isShowForm" title="角色详情" :width="1000" :is-show-footer="false" class="dictionaryBox" :is-click-modal-close="true" @closeDialog="onCancel">
		<el-row v-if="!isEmpty(showFields)" class="detailWrap">
			<template v-for="(item, index) in Object.keys(showFields)" :key="`${item}_${index.vue}`">
				<el-col :span="item === 'description' || item === 'menu' ? 24 : 12">
					<div class="labelWrap">{{ showFields[item] }}</div>
					<div v-if="item === 'createTime' || item === 'updateTime'" class="valueWrap">{{ day.getDay(editUserInfo[item]) || '-' }}</div>
					<div v-else-if="item === 'status'" class="valueWrap">
						<div class="status">
							<div :class="editUserInfo[item] ? 'ring' : 'ring1'"></div>
							<span class="state" :style="editUserInfo[item] ? 'color: #285FBB;' : 'color: #C0C4CC;'">{{ editUserInfo[item] ? '是' : '否'}}</span>
						</div>
					</div>
					<div v-else class="valueWrap">{{ editUserInfo[item] || '-' }}</div>
				</el-col>
			</template>
		</el-row>
	</Dialog>
</template>

<script setup>
import { ref, defineExpose } from 'vue';
import { isEmpty } from 'lodash';
import { day, echartsFit } from '@/utils';

const isShowForm = ref(false);

const editUserInfo = ref({}); // 编辑的用户信息

const showFields = ref({
	name: '角色名称',
	code: '角色编码',
	description: '描述',
	menu: '菜单权限'
});

const onCancel = () => {
	editUserInfo.value = {};
	isShowForm.value = false;
};

const openDailog = (userInfo) => {
	editUserInfo.value = userInfo;
	isShowForm.value = true;
};

defineExpose({
	openDailog,
});

</script>

<style lang="less" scoped>
@import '@/assets/styles/detail.less';
</style>