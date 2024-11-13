// 角色相关接口
import { get, post, put, remove } from '../index';

const apiPrefix = '/qkj/roles';

// 获取角色列表
export const getRoleList = async (param) => {
	try {
		const data = await get(`${apiPrefix}`, param);
		return data;
	} catch (err) {
		console.log(err);
	}
};

// 添加角色
export const addRole = async (formData) => {
	try {
		const data = await post(`${apiPrefix}/add`, {}, formData);
		return data;
	} catch (err) {
		console.log(err);
	}
};

// 编辑角色
export const editRole = async (formData) => {
	try {
		const data = await put(`${apiPrefix}/edit`, {}, formData);
		return data;
	} catch (err) {
		console.log(err);
	}
};

// 删除角色
export const removeRole = async (id) => {
	try {
		const data = await remove(`${apiPrefix}/del/${id}`);
		return data;
	} catch (err) {
		console.log(err);
	}
};

// 获取所有角色 
export const getAllRole = async (param) => {
	try {
		const data = await get(`${apiPrefix}/all`, param);
		return data;
	} catch (err) {
		console.log(err);
	}
};

// 角色授权
export const grantMenu = async (formData) => {
	try {
		const data = await post(`${apiPrefix}/grant`, {}, formData);
		return data;
	} catch (err) {
		console.log(err);
	}
};

