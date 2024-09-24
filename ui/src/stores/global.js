// import * as service from '@/api/user';
// import info from '../../public/js/info';

export default {
	namespaced: true,
	state: {
		width: document.documentElement.clientWidth, // 可视区域宽
		height: document.documentElement.clientHeight, // 可视区域高
		permissionObj: {}, // 菜单列表的操做数据
		account: 'username', // 用户帐户
		isDot: false, // 消息红点是否显示
	},
	mutations: {
		// 设置屏幕尺寸
		// this.$store.commit('global/setScreenSize', { width, height });
		setScreenSize: (state, { width, height }) => {
			state.width = width;
			state.height = height;
			// state.mainHeight = height - info.layout.otherHeight;
		},
		// 菜单列表的操做数据
		setPermissionObj: (state, { data }) => { state.permissionObj = data; },
		// 其他字段
		setField: (state, { data, name }) => { state[name] = data; },
		// 重置所有字段
		resetAllField: state => {
			state.width = document.documentElement.clientWidth; // 可视区域宽
			state.height = document.documentElement.clientHeight; // 可视区域高
			state.permissionObj = {};
			state.account = undefined; // 用户帐户
			state.isDot = false; // 用户帐户
		},
	},

	actions: {
		// ex: async getProjectDetail({ commit, state }, { values }) 第2个参数为dispatch的payload
		// 查询项目详情
		async getAccount({ commit }) {
			return new Promise(resolve => {
				console.log(commit, resolve);
			});
		},
		
	},
};
