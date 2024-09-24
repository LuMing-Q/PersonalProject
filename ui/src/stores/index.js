// 状态管理
import { createStore } from 'vuex';

import global from './global.js';
import options from './options.js';

const store = createStore({
	modules: {
		global, // 全局
		options, // 配置
	},
});

export default store;
