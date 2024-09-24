<!-- 审批、传阅签字 -->
<template>
	<el-row v-if="!isEmpty(props.fields)" class="detailWrap">
		<template v-for="(item, index) in Object.keys(props.fields)" :key="`${item}_${index.vue}`">
			<el-col :span="item !== 'description' && item !== 'abstract' && item !== 'serviceRemark' ? 12 : 24">
				<div class="labelWrap">{{ props.fields[item] }}</div>
				<div v-if="item === 'createTime' || item === 'updateTime'" class="valueWrap">{{ day.getDay(data[item]) || '-' }}</div>
				<div v-else class="valueWrap">{{ data[item] || '-' }}</div>
			</el-col>
		</template>
	</el-row>
</template>

<script setup>
import { defineProps } from 'vue';
import { isEmpty } from 'lodash';
import { day } from '@/utils';

const props = defineProps({
	fields: { type: Object, default: () => {} },
	data: { type: Object, default: () => {} }
});
</script>

<style lang="less" scoped>
@import '@/assets/styles/detail.less';
.detailWrap {
	padding: 0 72px 0 50px;
	margin-bottom: 8px;
}
</style>
