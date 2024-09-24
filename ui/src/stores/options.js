// 选项数据状态管理

import {
	getDictOption,
	getCategoryOption
} from '@/api/options';
import { session } from '@/utils/index.js';

export default {
	namespaced: true,
	state: {
		diskOptionsList: [], // 字典数据列表
		categoryOptionList: [], // 服务目录列表
	},

	mutations: {
		// 更新字典数据列表
		async handleDisk(state, data) {
			state.diskOptionsList = data;
		},
		// 更新服务目录数据列表
		async handleCategory(state, data) {
			state.categoryOptionList = data;
		},
		
	},

	actions: {
		// 获取字典数据
		async getDict(context) {
			const res = await getDictOption();
			context.commit('handleDisk', res.data);
		},

		// 获取服务目录列表
		async getCategory(context) {
			const res = await getCategoryOption();
			context.commit('handleCategory', res.data);
		},
	},
};
