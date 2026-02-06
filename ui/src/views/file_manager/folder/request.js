import axios from 'axios';
import { ElMessage } from 'element-plus';
import { createWebHistory } from 'vue-router';
import { session } from '@/utils';

const history = createWebHistory();
const service = axios.create({
	baseURL: `${window.g.BASE_URL}/qkj/compose/`,
	timeout: 600000 // 10分钟超时
});

// 请求拦截器：带上 token
service.interceptors.request.use(config => {
	const token = session.getStorage('token');
	if (token) {
		config.headers.Authorization = 'Bearer ' + token;
	}
	return config;
});

// 响应拦截器：统一处理 Result<T> 格式 + 401 弹窗
service.interceptors.response.use(
	response => {
		const res = response.data;
		if (res.code === 200) {
			return res.data;
		} else if (res.code == 401) {
			setTimeout(() => {
				ElMessage({ message: res.msg, type: 'error' });
			}, 500);
			if (window.location.pathname && window.location.pathname !== '/login') {
				session.setStorage('token', '');
				history.push(`/login?from=${window.location.pathname}`);
				location.reload();
			} else {
				session.clearStorageAll();
				history.push('/login');
			}
			return Promise.reject(new Error('未授权'));
		}
		ElMessage.error(res.msg || '请求失败');
		return Promise.reject(new Error(res.msg));
		
	},
	error => {
		ElMessage.error(error.message || '网络错误');
		return Promise.reject(error);
	}
);

export default service;
