<template>
	<div>
		<input ref="folderInput" type="file" webkitdirectory multiple hidden @change="handleFolderUpload" />
		<el-button type="primary" @click="folderInput.click()">
			<el-icon>
				<Folder />
			</el-icon>&nbsp;选择文件夹
		</el-button>

		<el-tree v-if="fileTree.length" :data="fileTree" :props="{ children: 'children', label: 'name' }" node-key="path"
			default-expand-all>
			<template #default="{ data }">
				<div style="display:flex; align-items:center; justify-content:space-between;">
					<span v-if="!data.folder && data.status === 'done'" style="margin-right:20px;color:green;">完成</span>
					<span>{{ data.name || data.fileName }}</span>
					<el-progress v-if="!data.folder && data.status !== 'done'" :percentage="data.progress" :stroke-width="12"
						style="flex:1; margin-left:8px;" />
				</div>
			</template>
		</el-tree>
	</div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import SparkMD5 from 'spark-md5';
import { Folder } from '@element-plus/icons-vue';
import { session } from '@/utils';

const folderInput = ref(null);
const fileTree = ref([]);
const CHUNK_SIZE = 5 * 1024 * 1024;
const MAX_RETRIES = 3;
const token = session.getStorage('token');
const newToken = ref(token ? `Bearer ${token}` : null);

const sleep = ms => new Promise(resolve => setTimeout(resolve, ms));

// --- 计算文件 hash ---
const calculateHash = (file) => new Promise(resolve => {
	const chunkSize = 20 * 1024 * 1024;
	const spark = new SparkMD5.ArrayBuffer();
	const reader = new FileReader();
	let cur = 0;
	const loadNext = () => {
		const slice = file.slice(cur, cur + chunkSize);
		reader.readAsArrayBuffer(slice);
	};
	reader.onload = e => {
		spark.append(e.target.result);
		cur += chunkSize;
		if (cur < file.size) loadNext();
		else resolve(spark.end());
	};
	loadNext();
});

// --- 查找节点 ---
const findNode = (nodes, path) => {
	for (const n of nodes) {
		if (n.path === path) return n;
		const found = findNode(n.children || [], path);
		if (found) return found;
	}
	return null;
};

// --- 保存状态到后端 ---
const saveFileStateToServer = async () => {
	const flatten = [];
	const traverse = nodes => {
		for (const node of nodes) {
			flatten.push({
				id: node.id,
				fileHash: node.fileHash,
				fileName: node.name || node.fileName,
				path: node.path,
				size: node.size,
				uploadedChunks: node.uploadedChunks || [],
				status: node.status,
				folder: node.folder,
				parentPath: node.parentPath,
			});
			if (node.children) traverse(node.children);
		}
	};
	traverse(fileTree.value);
	await axios.post(`${window.g.BASE_URL}/qkj/file/saveState`, flatten, {
		headers: { Authorization: newToken.value },
	});
};

// --- 上传分片 ---
const uploadChunk = async (item, index, totalChunks) => {
	const start = index * CHUNK_SIZE;
	const end = Math.min(item.size, start + CHUNK_SIZE);
	const chunk = item.file.slice(start, end);
	const formData = new FormData();
	formData.append('file', chunk);
	formData.append('fileUrl', item.parentPath || '');
	formData.append('fileHash', item.fileHash);
	formData.append('chunkIndex', index);
	formData.append('totalChunks', totalChunks);

	await axios.post(`${window.g.BASE_URL}/qkj/file/uploadChunk`, formData, {
		headers: { Authorization: newToken.value, 'Content-Type': 'multipart/form-data' },
		onUploadProgress: e => {
			if (e.total) item.progress = Math.min(
				Math.round(((item.uploadedChunks.length * CHUNK_SIZE + e.loaded) / item.size) * 100), 100
			);
		},
	});
};

// --- 分片上传重试 ---
const uploadChunkWithRetry = async (item, index, totalChunks) => {
	for (let attempt = 1; attempt <= MAX_RETRIES; attempt++) {
		try {
			await uploadChunk(item, index, totalChunks);
			if (!item.uploadedChunks.includes(index)) item.uploadedChunks.push(index);
			await saveFileStateToServer();
			return;
		} catch (err) {
			if (attempt === MAX_RETRIES) throw err;
			await sleep(1000);
		}
	}
};

// --- 恢复状态并检测 MinIO 实际文件 ---
let uploadedFileHashes = new Set();
const restoreStateFromServer = async () => {
	const { data: fileData } = await axios.get(`${window.g.BASE_URL}/qkj/file/getState`, {
		headers: { Authorization: newToken.value },
	});

	if (fileData && fileData.data.length) {
		const map = {};
		const tree = [];
		for (const f of fileData.data) {
			const node = { ...f, children: [], file: null, progress: 0 };
			map[f.path] = node;
		}

		for (const f of fileData.data) {
			const parent = f.parentPath ? map[f.parentPath] : null;
			if (parent) parent.children.push(map[f.path]);
			else tree.push(map[f.path]);
		}

		fileTree.value = tree;

		// 检查 MinIO 实际分片，只有存在的才标记完成
		for (const node of fileData.data) {
			if (!node.folder && node.fileHash) {
				const { data: uploadedChunksResp } = await axios.get(`${window.g.BASE_URL}/qkj/file/checkChunks`, {
					params: { fileHash: node.fileHash },
					headers: { Authorization: newToken.value },
				});
				node.uploadedChunks = uploadedChunksResp.data.data || [];
				const totalChunks = Math.ceil(node.size / CHUNK_SIZE);
				if (node.uploadedChunks.length === totalChunks) {
					node.status = 'done';
					node.progress = 100;
					uploadedFileHashes.add(node.fileHash);
				} else {
					node.status = 'waiting';
					node.progress = Math.floor((node.uploadedChunks.length / totalChunks) * 100);
				}
			}
		}
	}
};

onMounted(() => restoreStateFromServer());

// --- 处理文件夹上传 ---
const handleFolderUpload = async event => {
	const files = event.target.files;

	if (!files.length) return;

	for (const f of files) {
		if (f.name === '.DS_Store') {
			continue;
		}
		const path = f.webkitRelativePath || f.name;
		const segments = path.split('/');
		let parent = null;

		for (let i = 0; i < segments.length; i++) {
			const seg = segments[i];
			const nodePath = segments.slice(0, i + 1).join('/');
			let node = findNode(fileTree.value, nodePath);

			if (!node) {
				node = {
					name: seg,
					path: nodePath,
					children: [],
					file: i === segments.length - 1 ? f : null,
					size: i === segments.length - 1 ? f.size : 0,
					progress: 0,
					status: 'waiting',
					fileHash: null,
					uploadedChunks: [],
					folder: i !== segments.length - 1,
					parentPath: parent ? parent.path : null,
				};

				if (!node.folder && node.file) {
					node.fileHash = await calculateHash(node.file);
				}

				if (parent) parent.children.push(node);
				else fileTree.value.push(node);
			} else if (i === segments.length - 1) {
				node.file = f;
				node.size = f.size;
				node.fileHash = await calculateHash(f);
			}

			parent = node;
		}
	}

	// --- 上传未完成文件 ---
	const uploadQueue = [];
	const traverseFiles = nodes => {
		for (const n of nodes) {
			if (!n.folder && n.file && n.status !== 'done') uploadQueue.push(n);
			if (n.children) traverseFiles(n.children);
		}
	};
	traverseFiles(fileTree.value);

	for (const item of uploadQueue) {
		const totalChunks = Math.ceil(item.size / CHUNK_SIZE);

		// 获取已上传分片
		const { data: uploadedList } = await axios.get(`${window.g.BASE_URL}/qkj/file/checkChunks`, {
			params: { fileHash: item.fileHash },
			headers: { Authorization: newToken.value },
		});
		item.uploadedChunks = uploadedList.data.data || [];

		const chunkQueue = [];
		for (let i = 0; i < totalChunks; i++) {
			if (!item.uploadedChunks.includes(i)) chunkQueue.push(i);
		}

		const activeChunks = [];
		while (chunkQueue.length || activeChunks.length) {
			while (activeChunks.length < 3 && chunkQueue.length) {
				const index = chunkQueue.shift();
				const p = uploadChunkWithRetry(item, index, totalChunks).finally(() =>
					activeChunks.splice(activeChunks.indexOf(p), 1)
				);
				activeChunks.push(p);
			}
			await Promise.race(activeChunks);
		}

		// 合并分片
		await axios.post(`${window.g.BASE_URL}/qkj/file/mergeChunks`, {
			fileUrl: item.parentPath || '',
			fileName: item.file.name,
			fileHash: item.fileHash,
			totalChunks,
		}, { headers: { Authorization: newToken.value } });

		item.status = 'done';
		item.progress = 100;
		uploadedFileHashes.add(item.fileHash);
		await saveFileStateToServer();
	}
};
</script>
