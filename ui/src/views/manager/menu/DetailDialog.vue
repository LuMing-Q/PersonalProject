<template>
	<Dialog :is-show="isShowForm" title="菜单详情" :width="1260" :is-show-footer="false" class="dictionaryBox" :is-click-modal-close="true" @closeDialog="onCancel">
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
					<div v-else-if="item === 'type'" class="valueWrap">
						{{ editUserInfo[item] === '1' ? '菜单' : '操作' || '-' }}
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
import { day } from '@/utils';

const isShowForm = ref(false);

const editUserInfo = ref({}); // 编辑的用户信息

const showFields = ref({
	name: '菜单名称',
	type: '类别',
	parentName: '上级菜单',
	level: '所在层级',
	path: '页面地址',
	info: '描述'
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
</style>