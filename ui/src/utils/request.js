import axios from 'axios';
import jsonBig from 'json-bigint';
import { isEmpty } from 'lodash';
import { createWebHistory } from 'vue-router';
import { session } from '@/utils';

const history = createWebHistory();

/**
 * params 参数说明
 * notMessage: 不显示消息提示
 * notLoding: 不显示 loading 提示
 */

// 创建 axios 实例
const request = axios.create({
  baseURL: window.g.BASE_URL,
  timeout: 1800 * 1000,
  transformResponse: [
    (data) => {
      try {
        return jsonBig.parse(data);
      } catch {
        return data;
      }
    },
  ],
});

// ==================== Loading 管理器 ====================
class LoadingManager {
  #count = 0;
  #instance = null;

  show() {
    this.#count += 1;
    if (this.#count === 1) {
      this.#instance = ElLoading.service({
        lock: true,
        text: 'Loading',
        background: 'rgba(0, 0, 0, 0.2)',
      });
    }
  }

  hide() {
    if (this.#count > 0) this.#count -= 1;
    if (this.#count === 0 && this.#instance) {
      this.#instance.close();
      this.#instance = null;
    }
  }
}

const loadingManager = new LoadingManager();

// ==================== 配置提取器 ====================
/**
 * 从 axios 配置中提取显示消息和 loading 提示的配置
 * @param config axios 配置对象
 * cleanedParams 过滤掉 notMessage 和 notLoding 参数，返回后端用的纯净参数
 */
const extractConfig = (config) => ({
  showMessage: !config.params?.notMessage,
  showLoading: !config.params?.notLoding,
  cleanedParams: Object.fromEntries(
    Object.entries(config.params || {}).filter(
      ([key]) => !['notLoding', 'notMessage'].includes(key)
    )
  ),
});

// ==================== 响应处理器 ====================
const responseHandlers = {
  // 字符串响应错误
  handleStringResponse(res) {
    console.error('Request Error:', {
      method: res.config.method?.toUpperCase(),
      url: res.config.url,
      response: res,
    });
    return Promise.reject('类型错误: response.data 应为对象,而不是字符串');
  },

  // 增删改成功提示
  handleMutationSuccess(res, data, showMessage) {
    const isLogin = res.config.url === '/qkj/login';
    if (showMessage && !isLogin) {
      ElMessage({ message: data.msg, type: data.code === 200 ? 'success' : 'error' });
    }
    return data.code === 200 ? data : Promise.reject(data);
  },

  // Token 过期处理
  handleTokenExpired(data) {
		ElMessage({ message: data.msg || '登录已过期', type: 'error', duration: 2000 });
		setTimeout(() => {
			session.clearStorageAll();
			const currentPath = window.location.pathname;
			const isNotLogin = currentPath && currentPath !== '/login';
			if (isNotLogin) {
				history.push(`/login?from=${currentPath}`);
				location.reload();
			} else {
				history.push('/login');
			}
		}, 2000);
    return Promise.reject(data);
  },
};

// ==================== 错误处理器 ====================
const errorHandlers = {
  // HTTP 错误处理
  handleHttpError(err) {
    const { status, data } = err.response;
    const message = data?.message || data?.error || '请求失败';

    if (status === 503) {
      ElMessage({ message: '服务不可用', type: 'error' });
    } else {
      ElMessage({
        message: `${data?.path || ''}请求失败 ${status}: ${message}`,
        type: 'error',
      });
    }

    // Token 过期统一处理
    if (status === 401) {
      this.handleTokenExpired(data);
    }

    return Promise.reject({
      ok: false,
      message,
      status,
    });
  },

  // 网络错误处理
  handleNetworkError() {
    ElMessage({ message: '网络异常！', type: 'error' });
    return Promise.reject({ ok: false, message: '网络异常' });
  },
};

// ==================== 拦截器 ====================

// Request 拦截器
request.interceptors.request.use((config) => {
  const { showMessage, showLoading, cleanedParams } = extractConfig(config);
  
  // 将配置挂载到 config 上供 response 使用
  config._meta = { showMessage, showLoading };
  
  if (showLoading) loadingManager.show();

  // 设置 Token
  const token = session.getStorage('token');
  if (!isEmpty(token)) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  // 清理参数
  config.params = Object.keys(cleanedParams).length > 0 ? cleanedParams : undefined;

  return config;
});

// Response 拦截器
request.interceptors.response.use(
  (res) => {
    const { showMessage, showLoading } = res.config._meta || {};
    if (showLoading) loadingManager.hide();

    const data = res.data;

    // 字符串响应错误
    if (typeof data === 'string') {
      return responseHandlers.handleStringResponse(res);
    }

    // 401 统一处理
    if (data?.code === 401) {
      return responseHandlers.handleTokenExpired(data);
    }

    // 增删改操作
    const isMutation = ['post', 'put', 'delete', 'patch'].includes(res.config.method?.toLowerCase());
    if (isMutation) {
      return responseHandlers.handleMutationSuccess(res, data, showMessage);
    }

    // 普通查询请求
    if (res.status === 200 && data?.code === 200) {
      return data.data ?? data;
    }

    // 其他错误
    ElMessage({ message: data?.msg || '接口调用失败', type: 'error' });
    return Promise.reject(data);
  },
  (err) => {
    // 确保 loading 隐藏
    loadingManager.hide();

    // 有 HTTP 响应的错误
    if (err.response) {
      return errorHandlers.handleHttpError(err);
    }
    
    // 网络错误（无响应）
    return errorHandlers.handleNetworkError();
  }
);

export default request;