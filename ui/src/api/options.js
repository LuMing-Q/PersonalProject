// 选项相关数据请求

import { get, post } from '@/api';

const apiPrefix = 'wdis';

// 获取字典数据
export const getDictOption = async (params = {}) => {
	try {
		const res = await get(`${apiPrefix}/register/dict`, params);
		return res;
	} catch (err) {
		console.log(err);
	}
};

// 获取服务录目数据
export const getCategoryOption = async (params = {}) => {
	try {
		const res = await get(`${apiPrefix}/register/category`, params);
		return res;
	} catch (err) {
		console.log(err);
	}
};

// 数据转换
// JSON =》 对象数组
export const postParam = async (formData) => {
	try {
		const res = await post(`${apiPrefix}/transform/param`, { isNotMess: true }, formData);
		return res;
	} catch (err) {
		console.log(err);
	}
};

// 对象数组 =》 JSON
export const postJson = async (formData) => {
	try {
		const res = await post(`${apiPrefix}/transform/array`, { isNotMess: true }, formData);
		return res;
	} catch (err) {
		console.log(err);
	}
};
