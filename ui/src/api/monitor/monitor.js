// 角色相关接口
import { get, post, put, remove } from '../index';

const apiPrefix = '/qkj/monitor';

// 获取cpu负载信息
export const getCpuLoad = async () => {
	try {
		const data = await get(`${apiPrefix}/cpu_load`, { notLoding: true });
		return data;
	} catch (err) {
		console.log(err);
	}
};