<!--
 * @Author: 陆忠兵
 * @Description: text复制
-->
<template>
  <span class="clipboardTextWrap" @click="copy">
		<slot>
			<!-- <bz-icon :name="props.iconName" :size="props.iconSize" style="vertical-align: middle" /> -->
			复制
		</slot>
  </span>
</template>

<script setup>
import { defineProps } from 'vue';
import clipboard3 from 'vue-clipboard3';

const props = defineProps({
	copyText: { type: String, default: '' }, // 复制的文本
	showName: { type: String, default: 'icon-fuzhi' }, // 自定义复制图标名
	iconSize: { type: Number, default: 20 }
});

const { toClipboard } = clipboard3();

const copy = async () => {
	try {
		await toClipboard(props.copyText);
		ElMessage({
			type: 'success',
			message: '复制成功！'
		});
	} catch (error) {
		ElMessage({
			type: 'success',
			message: '复制成功！'
		});
	}
};
</script>

<style lang="less" scoped>
.clipboardTextWrap{
	margin-left: 14px;
	cursor: pointer;
	-webkit-user-select:none;
	-moz-user-select:none;
	-ms-user-select:none;
	user-select:none;
}
</style>
