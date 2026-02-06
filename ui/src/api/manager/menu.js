// 菜单相关接口
import { get, post, put, remove } from '../index';

const apiPrefix = '/qkj/menus';

// 查询所有菜单
export const getAllList = async () => {
	try {
		const list = await get(`${apiPrefix}/all`);
		return list;
	} catch (err) {
		console.log(err);
	}
};

// 查询分页菜单
export const getPage = async (params) => {
	try {
		const list = await get(`${apiPrefix}`, params);
		return list;
	} catch (err) {
		console.log(err);
	}
};

// 删除菜单
export const removeMenu = async (menuId) => {
	try {
		const list = await remove(`${apiPrefix}/${menuId}`);
		return list;
	} catch (err) {
		console.log(err);
	}
};

// 新建菜单
export const addMenu = async (formData) => {
	try {
		const list = await post(`${apiPrefix}`, {}, formData);
		return list;
	} catch (err) {
		console.log(err);
	}
};

// 修改菜单
export const editMenu = async (formData) => {
	try {
		const res = await put(`${apiPrefix}`, {}, formData);
		return res;
	} catch (err) {
		console.log(err);
	}
};

// 对角色授权菜单
export const addRoleMenu = async (formData) => {
	try {
		const res = await put(`${apiPrefix}/role`, {}, formData);
		return res;
	} catch (err) {
		console.log(err);
	}
};