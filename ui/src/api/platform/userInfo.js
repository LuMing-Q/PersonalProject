// 用户登录相关接口
import { get, post, put, remove } from '../index';

const apiPrefix = '/qkj';

// 用户登录。formData用户的登录信息
export const userLogin = async (formData) => {
	try {
		const userLoginInfo = await post(`${apiPrefix}/login`, {}, formData);
		return userLoginInfo;
	} catch (err) {
		console.log(err);
	}
};

// 获取用户菜单。roleId 为用户所属角色的角色id
export const getUserMenuList = async (roleId) => {
	try {
		const userMenuList = await get(`${apiPrefix}/menus/${roleId}`);
		return userMenuList;
	} catch (err) {
		console.log(err);
	}
};
