<!-- 条形进度条 -->
<template>
	<div class="progressWrap" :style="`height: ${props.height}px; border-radius: ${props.height / 2}px; background: ${getPercentageColor().background}; box-shadow: ${getPercentageColor().boxShadow}`">
		<div :style="`width: ${props.percentage}%;height: ${props.height}px; border-radius: ${props.height / 2}px; background: linear-gradient(90deg, ${getPercentageColor().startColor}, ${getPercentageColor().endColor})`"></div>
	</div>
</template>

<script setup>
import { ref, defineProps } from 'vue';

const props = defineProps({
	percentage: { type: Number, default: 0 }, // 进度数值
	height: { type: Number, default: 16 }, // 进度条高度
});

const getPercentageColor = () => {
	if (props.percentage < 70) {
		return { background: '#eeffef', startColor: '#9ee178', endColor: '#67c23a', boxShadow: '0 0 6px 0 #4a942740' };
	} else if (props.percentage > 90) {
		return { background: '#ffeae8', startColor: '#f77b77', endColor: '#ed4848', boxShadow: '0 0 6px 0 #c4353a40' };
	} 
	return { background: '#fff2cf', startColor: '#ffba42', endColor: '#f19a17', boxShadow: '0 0 6px 0 #cc780a40;' };
	
};

</script>

<style lang="less" scoped>
.progressWrap{
	width: 100%;
}
</style>