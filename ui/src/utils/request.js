import axios from 'axios';
import jsonBig from 'json-bigint';
import {isEmpty} from 'lodash';
import {createWebHistory} from 'vue-router';
import {session} from '@/utils';

const history = createWebHistory();

const request = axios.create({
  baseURL: window.g.BASE_URL,
  timeout: 1800 * 1000,
  transformResponse: [function (data) {
    try {
      return jsonBig.parse(data);
    } catch (err) {
      return data;
    }
  }],
});

// 全局加载提示
let loadingInstance = null; // loading实例
let isShowMessage = true; // 是否弹出请求接口反馈提示信息
let isMonitorRequest = true; // 是否显示Loading(加载遮罩层)
let requestCount = 0; // 请求次数

// 显示loading
const showLoading = () => {
  requestCount += 1;
  if (requestCount === 1) loadingInstance = ElLoading.service({ lock: true, text: 'Loading', background: 'rgba(0, 0, 0, 0.2)' });
};

// 隐藏loading
const hideLoading = () => {
  if (requestCount >= 1) requestCount -= 1;
  if (loadingInstance && requestCount === 0) loadingInstance.close();
};

request.interceptors.request.use(
	(config) => {
    isShowMessage =config.params && !config.params.notMessage;
		isMonitorRequest = config.params && !config.params.notLoding;
    if (isMonitorRequest) showLoading();
    const token = session.getStorage('token');
    if (token &&!isEmpty(token)) config.headers.Authorization = `Bearer ${token}`;
    if (config.params) {
      delete config.params.notLoding;
      delete config.params.notMessage;
    }
    return config;
  },
);

request.interceptors.response.use(
  (res) => {
    hideLoading();
		const r = res.data;
    const { method, url } = res.config;
    if (typeof r === 'string') {
      console.error('request method: ', method.toUpperCase());
      console.error('request url: ', res.config.ur);
      console.error('response info: ', res);
      const errMsg = '类型错误: response.data 应为对象,而不是字符串';
      ElMessage({ message: errMsg, type: 'error' });
      return Promise.reject(errMsg);
    }
    // 增删改请求成功后显示提示信息
		if (method === 'post' || method === 'put' || method === 'delete' || method === 'patch') {
			// 特殊处理 --> 登录接口和接口参数有 { notMessage: true } 不显示提示信息
      if (isShowMessage && url !== '/qkj/login') ElMessage({ message: r.msg, type: r.code === 200? 'success' : 'error' });
      if (r.code === 200) return r;
		}
    // token过期
    if (r && r.code === 401) {
      ElMessage({ message: r.msg, type: 'error' });
      if (window.location.pathname && window.location.pathname!== '/login') {
        session.setStorage('token', '');
        history.push(`/login?from=${window.location.pathname}`);
        location.reload();
      } else {
        session.clearStorageAll();
        history.push('/login');
      }
      return Promise.reject(r);
    }
    
    if (res && res.status === 200) {
      if (r && r.code === 200) {
        return r.data || r;
      }
    }
    ElMessage({ message: r? r.msg : '接口调用失败', type: 'error' });
    return Promise.reject(r);
  },
  (err) => {
    hideLoading();
    if (err && err.response && err.response.data) {
      const { data: { error, message, path, status } } = err.response;
      if (status === 503) {
        ElMessage({ message: '服务不可用', type: 'error' });
      } else {
        ElMessage({
          message: `${path}请求失败 ${status }: ${message}${error}`,
          type: 'error',
        });
      }
      // token过期
      if (status === 401) {
        session.clearStorageAll();
        if (window.location.pathname && window.location.pathname!== '/login') {
          history.push(`/login?from=${window.location.pathname}`);
          location.reload();
        } else {
          history.push('/login');
        }
      }
      return Promise.reject({
        ok: false, message:!isEmpty(message)? message : error, status,
      });
    } else {
      ElMessage({
        message: '网络异常！',
        type: 'error',
      });
    }
  },
);

export default request;