// 角色相关接口
import { get, post, put, remove } from '../index';

const apiPrefix = '/qkj';

// 获取角色列表
export const getRoleList = async () => {
	try {
		const list = await get(`${apiPrefix}/roles`);
		return list;
	} catch (err) {
		console.log(err);
	}
};

// 删除菜单
export const removeRole = async (menuId) => {
	try {
		const list = await remove(`${apiPrefix}/menus/${menuId}`);
		return list;
	} catch (err) {
		console.log(err);
	}
};

// 新建菜单
export const addMenu = async (formData) => {
	try {
		const list = await post(`${apiPrefix}/menus`, {}, formData);
		return list;
	} catch (err) {
		console.log(err);
	}
};

// 修改菜单
export const editMenu = async (formData) => {
	try {
		const res = await put(`${apiPrefix}/menus`, {}, formData);
		return res;
	} catch (err) {
		console.log(err);
	}
};