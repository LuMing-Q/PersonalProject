<template>
	<div class="p-4">
		<input ref="folderInput" type="file" webkitdirectory multiple hidden @change="handleFolderUpload" />
		<input ref="fileInput" type="file" hidden @change="handleSingleReupload" />
		<el-button type="primary" @click="folderInput.click()">
			<el-icon>
				<Folder />
			</el-icon> 选择文件夹
		</el-button>
		<el-table :data="uploadList" border style="width: 100%; margin-top: 20px">
			<el-table-column prop="name" label="文件名" />
			<el-table-column label="文件上传进度" width="200">
				<template #default="{ row }">
					<el-progress :percentage="row.progress" :status="row.status === 'error' ? 'exception' : undefined" />
				</template>
			</el-table-column>
			<el-table-column prop="status" label="状态" width="120" align="center" />
			<el-table-column label="操作" width="100" align="center">
				<template #default="{ row }">
					<el-button v-if="row.status === '上传异常' || row.status === '已上传'" size="small" type="primary" plain
						:disabled="row.status === '上传中'" @click="triggerReupload(row)">
						重新上传
					</el-button>
				</template>
			</el-table-column>
		</el-table>
	</div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import request from './request';
import SparkMD5 from 'spark-md5';
import { Folder } from '@element-plus/icons-vue';

const folderInput = ref(null);
const fileInput = ref(null);
const uploadList = ref([]);
const CHUNK_SIZE = 20 * 1024 * 1024; // 20MB
const MAX_RETRY = 3;
const CONCURRENT_FILE_LIMIT = 3; // 同时上传文件数
const CONCURRENT_CHUNK_LIMIT = 3; // 单个文件同时上传分片数
// 当前要重新上传的文件对象
const reuploadTarget = ref(null);

// 计算文件MD5
async function calcMD5 (file) {
	const spark = new SparkMD5.ArrayBuffer();
	const chunkSize = CHUNK_SIZE;
	const chunks = Math.ceil(file.size / chunkSize);
	let currentChunk = 0;

	return new Promise((resolve, reject) => {
		const fileReader = new FileReader();
		function loadNext () {
			const start = currentChunk * chunkSize;
			const end = Math.min(file.size, start + chunkSize);
			fileReader.readAsArrayBuffer(file.slice(start, end));
		}
		fileReader.onload = e => {
			spark.append(e.target.result);
			currentChunk += 1;
			if (currentChunk < chunks) loadNext();
			else resolve(spark.end());
		};
		fileReader.onerror = reject;
		loadNext();
	});
}

// 更新UI状态
const updateStatus = (name, progress, status) => {
	const item = uploadList.value.find(i => i.name === name);
	if (item) {
		item.progress = Math.min(progress, 100);
		item.status = status;
	}
};

// 上传单个分片（带重试）
async function uploadChunk (md5, idx, chunk) {
	let attempt = 0;
	while (attempt < MAX_RETRY) {
		try {
			const objectName = `temporaryStorageFolder/${md5}_part${idx}`;
			await request.post(`upload/stream?object_name=${encodeURIComponent(objectName)}`,
				chunk,
				{
					headers: { 'Content-Type': 'application/octet-stream' }
				});
			return;
		} catch (err) {
			attempt += 1;
			console.warn(`分片${idx} 上传失败，第${attempt}次重试`);
			if (attempt >= MAX_RETRY) throw err;
			await new Promise(r => setTimeout(r, 1000 * attempt));
		}
	}
}

// 文件分片并发上传
async function uploadChunksConcurrently (md5, file, missing, uploaded) {
	const totalChunks = missing.length;
	const queue = [...missing];
	async function next () {
		if (!queue.length) return;
		const idx = queue.shift();
		const start = (idx - 1) * CHUNK_SIZE;
		const end = Math.min(file.size, start + CHUNK_SIZE);
		const chunk = file.slice(start, end);
		await uploadChunk(md5, idx, chunk);
		uploaded += 1;
		const progress = Math.round((uploaded / totalChunks) * 95);
		updateStatus(file.webkitRelativePath, progress, '上传中');
		await next();
	}
	const promises = [];
	for (let i = 0; i < CONCURRENT_CHUNK_LIMIT; i += 1) {
		promises.push(next());
	}
	await Promise.all(promises);
}

// 上传单个文件
async function uploadFile (file, isReupload) {
	const fileName = file.name;
	updateStatus(file.webkitRelativePath, 0, '计算中...');
	const md5 = await calcMD5(file);
	const totalSize = file.size;
	const chunkNum = Math.ceil(totalSize / CHUNK_SIZE);
	const fileUrl = file.webkitRelativePath.replace(fileName, '');
	try {
		// 初始化
		const init = await request.get('init', {
			params: {
				md5,
				totalSize,
				chunkSize: CHUNK_SIZE,
				chunkNum,
				fileUrl,
				fileName,
				reUpload: isReupload
			}
		});
		const objectName = init.objectName;
		if (init.done) {
			updateStatus(file.webkitRelativePath, 100, '已上传');
			return;
		}
		// 获取缺失分片
		const missing = await request.get('missing', {
			params: { md5, chunkNum, chunkSize: CHUNK_SIZE }
		});
		// 计算已上传分片数
		let uploaded = chunkNum - missing.length;
		updateStatus(file.webkitRelativePath, Math.round((uploaded / chunkNum) * 95), '上传中');
		// 并发上传分片
		await uploadChunksConcurrently(md5, file, missing, uploaded);
		// 合并文件
		updateStatus(file.webkitRelativePath, 100, '合并中...');
		await request.post('merge', null, {
			params: { md5, chunkNum, objectName }
		});
		updateStatus(file.webkitRelativePath, 100, '已上传');
	} catch (e) {
		console.error(e);
		updateStatus(file.webkitRelativePath, 0, '上传异常');
		ElMessage.error(`${fileName} 上传失败 ${e}`);
	}
}

// 并发上传文件工具
async function concurrentUpload (files, limit) {
	const queue = [...files];
	const promises = [];
	async function next () {
		if (!queue.length) return;
		const file = queue.shift();
		await uploadFile(file, false);
		await next();
	}
	for (let i = 0; i < limit; i += 1) {
		promises.push(next());
	}
	await Promise.all(promises);
}

// 上传文件夹入口
async function handleFolderUpload (e) {
	const files = Array.from(e.target.files);
	if (!files.length) return;
	uploadList.value = files.map(f => ({
		name: f.webkitRelativePath,
		progress: 0,
		status: '待上传'
	}));
	await concurrentUpload(files, CONCURRENT_FILE_LIMIT);
}

// 点击“重新上传”按钮
function triggerReupload (row) {
	reuploadTarget.value = row;
	fileInput.value.click(); // 触发选择单个文件
}

// 处理选择的文件（用于重新上传）
async function handleSingleReupload (e) {
	const file = e.target.files[0];
	if (!file) return;
	if (!reuploadTarget.value) return;
	// 原表格中保存的完整路径
	const originalPath = reuploadTarget.value.name;

	// 给 file 打补丁，补上原路径
	Object.defineProperty(file, 'webkitRelativePath', {
		value: originalPath,
		writable: false
	});
	// 更新状态
	updateStatus(reuploadTarget.value.name, 0, '重新上传中...');
	await uploadFile(file, true);

	// 清空 input 以防止下次选择同名文件无法触发 change
	fileInput.value.value = '';
	reuploadTarget.value = null;
}
</script>


<style scoped>
.p-4 {
	padding: 16px;
}
</style>
