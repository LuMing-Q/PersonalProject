<template>
  <div class="file-preview-box1">
    <!-- 图片 -->
    <img v-if="isImage" :src="imgUrl" />

    <!-- 视频 -->
    <video v-else-if="isVideo" :src="fileUrl" controls></video>

    <!-- PDF -->
		<iframe v-else-if="isPdf" :src="pdfUrl" width="100%" height="100%"></iframe>

    <!-- Word -->
    <div v-else-if="isWord" class="txt-box-style" v-html="fileContent"></div>

		<!-- txt -->
		<div v-else-if="isTxt" class="txt-box-style">
			<pre style="margin: 0;">{{ fileContent }}</pre>
		</div>

    <!-- CSV -->
		<div v-else-if="isCsv" class="txt-box-style">
			<table>
				<thead>
					<tr>
						<th v-for="(header, index) in csvData[0]" :key="index">{{ header }}</th>
					</tr>
				</thead>
				<tbody>
					<tr v-for="(row, rowIndex) in csvData.slice(1)" :key="rowIndex">
						<td v-for="(cell, cellIndex) in row" :key="cellIndex">{{ cell }}</td>
					</tr>
				</tbody>
			</table>
		</div>

		<!-- xlsx -->
		<div v-else-if="isXlsx && xlsxData.length" class="txt-box-style">
			<table>
				<thead>
					<tr>
						<th v-for="(header, index) in xlsxData[0]" :key="index" style="padding: 1px 30px;">{{ header }}</th>
					</tr>
				</thead>
				<tbody>
					<tr v-for="(row, rowIndex) in xlsxData.slice(1)" :key="rowIndex">
						<td v-for="(cell, cellIndex) in row" :key="cellIndex">{{ cell }}</td>
					</tr>
				</tbody>
			</table>
		</div>

    <!-- 默认下载链接 -->
		<div v-else style="text-align: center;">
			<div>当前文件暂不支持预览,请下载后查看</div><br>
			<a :href="fileUrl" download>下载</a>
		</div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, defineProps } from 'vue';
import axios from 'axios';
import mammoth from 'mammoth';
import * as XLSX from 'xlsx';
import Papa from 'papaparse';
import { preview } from '@/api/file';

const props = defineProps({
	filename: { type: String, default: '' }
});
// 创建响应式数据
const fileUrl = ref('');
const pdfUrl = ref('');
const imgUrl = ref('');
const fileContent = ref('');
const csvData = ref([]);
const xlsxData = ref([]);

// 计算属性来确定文件类型
const isImage = computed(() => /\.(jpeg|jpg|gif|png)$/i.test(fileUrl.value));
const isVideo = computed(() => /\.(mp4|webm|ogg)$/i.test(fileUrl.value));
const isPdf = computed(() => /\.pdf$/i.test(fileUrl.value));
const isWord = computed(() => /\.docx$/i.test(fileUrl.value));
const isTxt = computed(() => /\.(txt|md)$/i.test(fileUrl.value));
const isCsv = computed(() => /\.(csv)$/i.test(fileUrl.value));
const isXlsx = computed(() => /\.(xlsx)$/i.test(fileUrl.value));

// 获取文件 URL 和内容
const fetchFile = async (filename) => {
	fileUrl.value = filename;
	try {
		const response = await preview({ objectName: filename });
		if (response.status == 200) {
			const url = URL.createObjectURL(response.data);
			if (isWord.value) {
				const arrayBuffer = await response.data.arrayBuffer();
				const result = await mammoth.convertToHtml({ arrayBuffer });
				fileContent.value = result.value;
			} else if (isPdf.value) {
				pdfUrl.value = url;
				// await renderPdf();
			} else if (isTxt.value) {
				const contentResponse = await axios.get(url, { responseType: 'text' });
				fileContent.value = contentResponse.data;
			} else if (isImage.value) {
				imgUrl.value = url;
			} else if (isCsv.value) {
				const text = await response.data.text();
				Papa.parse(text, {
					header: false,
					complete: (result) => {
						csvData.value = result.data;
					}
				});
			} else if (isXlsx.value) {
				const arrayBuffer = await response.data.arrayBuffer();
				const workbook = XLSX.read(arrayBuffer, { type: 'array' });
				const sheetName = workbook.SheetNames[0];
				const worksheet = workbook.Sheets[sheetName];
				const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 });
				xlsxData.value = jsonData;
			} else {
				fileUrl.value = url;
			}
		}
	} catch (error) {
		console.error('Error fetching file content', error);
	}
};

onMounted(() => {
	fetchFile(props.filename);
});
</script>

<style lang="less" scoped>
.file-preview-box1 {
	display: flex;
	justify-content: center;
	align-items: center;
	height: 100%;
	width: 100%;
	background: #FFF;

	.txt-box-style {
		padding: 10px 0;
		width: 100%;
		height: calc(100% - 20px);
		overflow-y: auto;
		display: flex;
		justify-content: center;
		table {
			tr {
				th, td {
					padding: 1px 30px;
					text-align: center;
				}
			}
		}
	}
}
</style>
