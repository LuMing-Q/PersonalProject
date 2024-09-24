<template>
	<div class="word-preview-box">
		<vue-office-docx
			v-if="props.fileType === 'docx'"
			:src="props.fileData"
			style="height: 100%;"
			@rendered="rendered"
    />
		
		<vue-office-excel
			v-if="props.fileType === 'excel'"
			:src="props.fileData"
			style="height: 100%;"
			@rendered="rendered"
			@error="errorHandler"
    />

		<vue-office-pdf
			v-if="props.fileType === 'pdf'"
			:src="props.fileData"
			style="height: 100%;"
			@rendered="rendered"
			@error="errorHandler"
    />
	</div>
</template>

<script setup>
import { watch, defineProps } from 'vue';

import VueOfficeDocx from '@vue-office/docx';
import '@vue-office/docx/lib/index.css';

import VueOfficeExcel from '@vue-office/excel';
import '@vue-office/excel/lib/index.css';

import VueOfficePdf from '@vue-office/pdf';

const props = defineProps({
	fileData: { type: String, default: 'http://static.shanhuxueyuan.com/test6.docx' },
	fileType: { type: String, default: 'docx' },
});

const rendered = () => {
	console.log('渲染完成');
};

const errorHandler = () => {
	console.log('渲染失败');
};

</script>

<style lang="less" scoped>
.word-preview-box{
	height: 100%;
}
</style>
