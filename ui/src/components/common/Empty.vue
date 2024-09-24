<!-- 
	isLine 是否显示在一行
 -->
<template>
	<div class="empty_box">
		<div :class="props.isLine ? 'img_box2' : 'img_box1'">
			<img v-if="!props.isLine" src="@/assets/images/empty_big.png" class="imgStyle" alt="" srcset="">
			<img v-else src="@/assets/images/empty.png" class="imgStyle" alt="" srcset="">
			<div class="text_box">暂无数据...</div>
		</div>
	</div>
</template>
<script setup name="empty">
import { defineProps } from 'vue';

const props = defineProps({
	isLine: { type: Boolean, default: false }
});
</script>
<style lang="less" scoped>
.empty_box {
	padding: 20px 0;
	height: calc(100% - 40px);
	width: 100%;
	display: flex;
	justify-content: center;
	align-items: center;
	background: #fff;
	border-radius: 4px;

	.img_box1 { 
		text-align: center;

		.imgStyle {
			height: 206px;
			width: 268px;
		}

		.text_box {
			font-size: 16px;
			font-weight: 400;
			color: #909399;
		}
	}

	.img_box2 { 
		display: flex;

		.imgStyle {
			height: 123px;
			width: 160px;
			margin-right: 17px;
		}

		.text_box {
			font-size: 14px;
			font-weight: 400;
			color: #909399;
			height: 123px;
			line-height: 123px;
		}
	}
}
</style>