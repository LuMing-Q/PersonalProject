// 文件相关接口

import { post, remove } from '@/api';
import axios from 'axios';

const apiPrefix = '/qkj/file';

// 上传文件
export const upload = async (formdate) => {
	try {
		const response = await post(`${apiPrefix}`, {}, formdate);
		return response;
	} catch (err) {
		console.log(err);
	}
};

// 删除文件
export const removeFile = async (id, param) => {
	try {
		const data = await remove(`${apiPrefix}/${id}`, param);
		return data;
	} catch (err) {
		console.log(err);
	}
};

// 文件预览
export const preview = async (params) => {
	try {
		const response = await axios.get(`${window.g.BASE_URL}/qkj/file/file_content`, {
			params,
			responseType: 'blob'
		});
		return response;
	} catch (err) {
		console.log(err);
	}
};