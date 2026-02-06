<template>
	<div class="p-4">
		<!-- 单文件选择 -->
		<input ref="fileInput" type="file" hidden @change="handleFileUpload" />

		<el-button type="primary" @click="fileInput.click()">
			选择文件
		</el-button>

		<el-table v-if="uploadList.length" :data="uploadList" border style="width: 100%; margin-top: 20px">
			<el-table-column prop="name" label="文件名" />
			<el-table-column label="上传进度" width="220">
				<template #default="{ row }">
					<el-progress :percentage="row.progress" :status="row.status === '上传异常' ? 'exception' : undefined" />
				</template>
			</el-table-column>

			<el-table-column prop="status" label="状态" width="120" align="center" />

			<el-table-column label="操作" width="120" align="center">
				<template #default="{ row }">
					<el-button v-if="row.status === '上传异常' || row.status === '已上传'" size="small" type="primary" plain
						@click="triggerReupload">
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
import { request } from '@/utils';
import SparkMD5 from 'spark-md5';

const fileInput = ref(null);
const uploadList = ref([]);

const CHUNK_SIZE = 20 * 1024 * 1024; // 20MB
const MAX_RETRY = 3;
const CONCURRENT_CHUNK_LIMIT = 3;

let currentFile = null;

/* ================== MD5 ================== */
async function calcMD5 (file) {
	const spark = new SparkMD5.ArrayBuffer();
	const chunks = Math.ceil(file.size / CHUNK_SIZE);
	let current = 0;

	return new Promise((resolve, reject) => {
		const reader = new FileReader();
		function loadNext () {
			const start = current * CHUNK_SIZE;
			const end = Math.min(file.size, start + CHUNK_SIZE);
			reader.readAsArrayBuffer(file.slice(start, end));
		}
		reader.onload = e => {
			spark.append(e.target.result);
			current++;
			if (current < chunks) loadNext();
			else resolve(spark.end());
		};
		reader.onerror = reject;
		loadNext();
	});
}

/* ================== UI ================== */
function updateStatus (progress, status) {
	const item = uploadList.value[0];
	if (!item) return;
	item.progress = Math.min(progress, 100);
	item.status = status;
}

/* ================== 分片上传 ================== */
async function uploadChunk (md5, idx, chunk) {
	let retry = 0;
	while (retry < MAX_RETRY) {
		try {
			await request.post(
				`/qkj/compose/upload/stream?object_name=${encodeURIComponent(`temporaryStorageFolder/${md5}_part${idx}`)}`,
				chunk,
				{ headers: { 'Content-Type': 'application/octet-stream' } }
			);
			return;
		} catch {
			retry++;
			if (retry >= MAX_RETRY) throw new Error(`分片 ${idx} 上传失败`);
			await new Promise(r => setTimeout(r, retry * 1000));
		}
	}
}

async function uploadChunks (md5, file, missing, uploaded) {
	const queue = [...missing];
	const total = missing.length;

	async function worker () {
		while (queue.length) {
			const idx = queue.shift();
			const start = (idx - 1) * CHUNK_SIZE;
			const end = Math.min(file.size, start + CHUNK_SIZE);
			await uploadChunk(md5, idx, file.slice(start, end));
			uploaded++;
			updateStatus(Math.round((uploaded / total) * 95), '上传中');
		}
	}

	await Promise.all(
		Array.from({ length: CONCURRENT_CHUNK_LIMIT }).map(worker)
	);
}

/* ================== 文件上传 ================== */
async function uploadFile (file, isReupload = false) {
	try {
		updateStatus(0, '计算中...');
		const md5 = await calcMD5(file);

		const chunkNum = Math.ceil(file.size / CHUNK_SIZE);

		const init = await request.get('/qkj/compose/init', {
			params: {
				md5,
				total_size: file.size,
				chunk_size: CHUNK_SIZE,
				chunk_num: chunkNum,
				file_name: file.name,
				re_upload: isReupload
			}
		});

		if (init.done) {
			updateStatus(100, '已上传');
			return;
		}

		const missing = await request.get('/qkj/compose/missing', {
			params: { md5, chunk_num: chunkNum, chunk_size: CHUNK_SIZE }
		});

		let uploaded = chunkNum - missing.length;
		updateStatus(Math.round((uploaded / chunkNum) * 95), '上传中');

		await uploadChunks(md5, file, missing, uploaded);

		updateStatus(100, '合并中...');
		await request.post('/qkj/compose/merge', null, {
			params: { md5, chunk_num: chunkNum, object_name: init.objectName }
		});

		updateStatus(100, '已上传');
	} catch (e) {
		console.error(e);
		updateStatus(0, '上传异常');
		ElMessage.error('上传失败');
	}
}

/* ================== 事件 ================== */
function handleFileUpload (e) {
	const file = e.target.files[0];
	if (!file) return;

	currentFile = file;
	uploadList.value = [{
		name: file.name,
		progress: 0,
		status: '待上传'
	}];

	uploadFile(file);
	fileInput.value.value = '';
}

function triggerReupload () {
	if (!currentFile) return;
	uploadFile(currentFile, true);
}
</script>
<style scoped>
.p-4 {
	padding: 16px;
}
</style>
