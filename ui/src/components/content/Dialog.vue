<!-- 弹窗样式 -->

<template>
	<el-dialog
		v-model="isVisible"
		:show-close="false"
		:width="typeof width === 'string' && width.indexOf('%') !== -1 ? width : echartsFit(width)"
		:before-close="closeDialog"
		:close-on-click-modal="props.isClickModalClose"
		:close-on-press-escape="props.isCloseOnPressEscape"
		align-center
		class="dialogWrap"
	>
		<template #header>
			<div class="dialogHeader">
				<slot name="dialogHeader">
					<div class="left">
						<h4>
							{{ props.title }}
							<span>
								<el-tooltip v-if="session.getStorage('isTips') && props.tipText" popper-class="formTooltip" effect="dark" :content="props.tipText" placement="bottom-start">
									<bz-icon name="icon-zhushi" :size="14" color="#3E7EE8" class="iconSize14" />
								</el-tooltip>
							</span>
						</h4>
						<p v-if="props.showTiPText">{{ props.showTiPText }}</p>
					</div>
					<el-button v-if="props.showClose" text @click="closeDialog">
						<el-icon class="el-icon--left"><CloseBold /></el-icon>
					</el-button>
				</slot>
			</div>
		</template>
		
		<!-- 内容区域 -->
		<div :class="`${!isTabs ? 'content' : isShowFooter ? 'tabsContent' : 'detailContent'}`">
			<slot />
		</div>

		<slot name="dialogFooter">
			<div v-if="props.isShowFooter" class="footer">
				<el-button :loading="isLoading" @click="closeDialog">取消</el-button>
				<el-button :loading="isLoading" type="primary" @click="onSubmit">确定</el-button>
			</div>
		</slot>
	</el-dialog>
</template>

<script setup name="BzDialog">
import { defineProps, defineEmits, watch, ref } from 'vue';
import { ElButton, ElDialog, ElIcon } from 'element-plus';
import { CloseBold } from '@element-plus/icons-vue';

import { session, echartsFit } from '@/utils';

const isVisible = ref(false);

const props = defineProps({
	// 按钮loading
	isLoading: { type: Boolean, default: false },
	// 右上角关闭按钮是否显示
	showClose: { type: Boolean, default: true },
	isClickModalClose: { type: Boolean, default: false }, // 是否可点击Modal关闭
	isCloseOnPressEscape: { type: Boolean, default: false }, // 是否可按下ESC关闭
	isShow: { type: Boolean, default: true }, // 是否显示弹窗
	width: { type: [ Number, String ], default: 480 }, // 弹窗宽度
	title: { type: String, default: '弹窗' }, // 弹窗标题
	isShowFooter: { type: Boolean, default: true }, // 是否显示footer（自定义时不生效；详情时必须传false）
	tipText: { type: String, default: '' }, // 开启提示的提示内容
	showTiPText: { type: String, default: '' }, // 弹窗header部分展示的提示内容
	isTabs: { type: Boolean, default: false }, // 是否为tabs内容
});
const emits = defineEmits(['closeDialog', 'onSubmit']);

const closeDialog = () => {
	emits('closeDialog');
};

const onSubmit = () => {
	emits('onSubmit');
};

watch(
	() => props.isShow,
	(value) => isVisible.value = value,
	{ immediate: true },
);

</script>

<style lang="less" scoped>
.footer{
	margin-top: 10px;
	float: right;
	.el-button{
		height: 30px;
	}
}
</style>

<style lang="less">
.dialogWrap{
	padding: 0;
	border-radius: 2px;
  .dialogHeader{
		height: 15px;
		line-height: 15px;
		font-size: 15px;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
		.left{
			display: flex;
			justify-content: flex-start;
			p{
				margin-left: 16px;
				font-size: 14px;
				color: #939393;
			}
		}
    h4{
      font-weight: 400;
      font-size: 14px;
      color: #323232;
      line-height: 15px;
    }
		.el-button.is-text {
			height: 15px;
			padding: 0 0;
		}
  }
  .el-dialog__header{
    margin: 0;
		padding: 20px 23px 16px;
		border-bottom: 1px solid #EAEDF1;
  }
  .el-dialog__body {
		.detailContent {
    	padding: 32px 24px 80px;
		}

		.tabsContent{
			padding: 0 0 16px;
		}
		
		.content {
			padding: 32px 24px 40px;
		}

		.footer {
			display: flex;
			width: calc(100% - 40px);
			justify-content: end;
			padding: 17px 20px 16px;
			margin-top: 0;
			border-top: 1px solid #EAEDF1;
			box-shadow: 0 -6px 4px 0 #0000000a;
		}

		.status{
			width: 100%;
			display: flex;
			align-items: center;
			justify-content: start;
			color: #30333A;
			font-size: 14px;
			font-weight: 400;
			// font-family: Microsoft YaHei;
		}
		.ring {
			width: 13px;
			height: 13px;
			border: 1px solid @brand-color-primary;
			border-radius: 50%;
			display: flex;
			justify-content: center;
			align-items: center;
			margin-right: 13px;
		}
		.ring::before {
			content: "";
			border-radius: 50%;
			background: @brand-color-primary;
			width: 6px;
			height: 6px;
		}
		.ring1 {
			width: 13px;
			height: 13px;
			border: 1px solid @text-color-disabled;
			border-radius: 50%;
			display: flex;
			justify-content: center;
			align-items: center;
			margin-right: 13px;
		}
		.ring1::before {
			content: "";
			border-radius: 50%;
			background: @text-color-disabled;
			width: 6px;
			height: 6px;
		}
  }
}

// 弹窗提示
.el-popper.is-dark.formTooltip{
	max-width: 400px;
}
</style>

