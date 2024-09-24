<!-- 文件预览 -->
<template>
	<div class="file-preview-wrap">
		<!-- <word-preview /> -->
		<file-preview :filename="props.filename"/>
	</div>
</template>

<script setup>
import { defineProps } from 'vue';
// import WordPreview from './filePreviewComponents/WordPreview.vue';
import FilePreview from './FilePreview.vue';
const props = defineProps({
	filename: { type: String, default: '' }
});

</script>

<style lang="less" scoped>
.file-preview-wrap{
	height: 100%;
}
</style>
