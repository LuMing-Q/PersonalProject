<!-- 审批、传阅签字 -->
<template>
	<div v-if="!isEmpty(props.data)" class="sign-card-box">
		<h3>
			<bz-icon name="icon-shenpi" :size="echartsFit(16)" />
			<span :style="`margin-left: ${echartsFit('8px')};`">{{ props.data.title }}</span>
		</h3>
		<div :class="`content-list-box ${props.data?.list?.length > 1 ? '' : 'padding-left-15'}`">
			<div v-for="(item, index) in props.data?.list" :key="`${index}_signCard`" class="content-box">
				<div class="left">
					{{ item.suggestion }}
				</div>
				<div class="right">
					<span>{{ item.userName }}</span>
					<!-- <img src="@/assets/images/test_name.png"> -->
					<span>时间：{{ day.getDay(item.createDate, 'YYYY-MM-DD') }}</span>
				</div>
			</div>
		</div>
	</div>
</template>

<script setup>
import { defineProps } from 'vue';
import { isEmpty } from 'lodash';
import { day, echartsFit } from '@/utils';

const props = defineProps({
	data: { type: Object, default: () => {} }
});
</script>

<style lang="less" scoped>
.padding-left-15{
	padding-left: 15px;
}

.sign-card-box{
	height: calc(~"100% - 25px");
	padding: 15px 21px 10px;
	border: 1px solid @component-color-divider;
	border-radius: 4px;

	h3{
		display: flex;
		align-items: center;
		line-height: 34px;
		font-size: 14px;
		font-weight: bold;
		margin-bottom: 24px;
		color: @text-color-primary;
	}

	.content-list-box{
		overflow-y: auto;
	}

	.content-box{
		padding: 0 0 10px 10px;
		display: flex;
		justify-content: space-between;

		& + .content-box {
			padding-top: 18px;
			border-top: 1px solid @component-color-divider;
		}

		& > div{
			flex: 1;
			flex-shrink: 0;
		}
		.right > span,
		.left{
			display: flex;
			flex-direction: column;
			justify-content: flex-end;
		}
		.left{
			color: @brand-color-primary;
			line-height: 18px;
			font-size: 14px;
		}
		.right{
			display: flex;
			justify-content: space-between;
			align-items: end;
			margin-left: 12px;
			max-width: 260px;

			img {
				width: 101px;
				height: 36px;
			}
		}
	}
}

</style>
