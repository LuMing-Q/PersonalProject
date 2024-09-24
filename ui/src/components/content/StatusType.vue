<template>
	<span :class="['status_component', statusClassList[props.statusCode]]">{{ statusList[props.statusCode] || '其他' }}</span>
</template>

<script setup name="StatusType">
import { ref, defineProps } from 'vue';

const props = defineProps({
	statusCode: { type: Number, default: 0 }, // 对应的状态字段
});

// 状态 1-已注册，2-发布中，3-已发布，4-下线中，5-已下线, 6-审核失败，7-入库成功，8-入库失败, 0-已删除
const statusList = ref(['已删除', '已注册', '发布中', '已发布', '下线中', '已下线', '下线失败', '入库成功', '入库失败', '删除中']);
const statusClassList = ref(['stop', 'registered', 'in-release', 'have-launched', 'offline', 'be-offline', 'audit-failure', 'success', 'error', 'deleting']);

</script>

<style lang="less" scoped>
@import '@/assets/styles/state.less';
.status_component {
	display: inline-block;
	padding: 0 10px;
	border: 1px solid;
	height: 30px;
	line-height: 30px;
	border-radius: 4px;
  font-size: 14px;
	font-weight: 400;
}
</style>