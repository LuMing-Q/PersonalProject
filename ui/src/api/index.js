/*
 * @LastEditTime: 2023-11-20
 * @Description: 异步请求服务
 */
import { request } from '@/utils';

/**
 * post请求-常用做创建/更新实体
 * @param url 接口地址
 * @param params 请求参数
 * @param values 请求体
 * @returns response
 */
export const post = (url = '', params = {}, values = {}) => request({
	url,
	method: values.id ? 'PUT' : 'POST',
	params,
	data: values,
});

/**
 * post请求(请求体里有id, 区别于常规的post更新实体)
 * @param {*} url 
 * @param {*} params 
 * @param {*} values 
 * @returns 
 */
export const postService = (url = '', params = {}, values = {}) => request({
	url,
	method: 'POST',
	params,
	data: values,
});

/**
 * put请求
 * @param url 接口地址
 * @param params 请求参数
 * @param values 请求体
 * @returns response
 */
export const put = (url = '', params = {}, values = {}) => request({
	url,
	method: 'PUT',
	params,
	data: values,
});

/**
 * get请求
 * @param url 接口地址
 * @param params 查询参数
 * @returns response
 */
export const get = (url = '', params = {}, isLoading = true) => request({ url, params, isLoading });

/**
 * getList分页请求
 * @param url 接口地址
 * @param params 查询参数
 * @returns response
 */
export const getList = (url = '', params = {}) => {
	// params.page -= 1; // 下标减1, 从0 开始
	return request({ url, params });
};

/**
 * delete请求
 * @param url 接口地址
 * @param params 条件参数
 * @param params 条件 body
 * @returns response
 */
export const remove = (url = '', params = {}, values = {}) => request({
	url,
	method: 'DELETE',
	params,
	data: values,
});
/**
 * patch请求
 * @param url 接口地址
 * @param params 查询参数
 * @returns response
 */
export const patch = (url = '', params = {}) => request({
	url,
	method: 'PATCH',
	data: params,
});
/**
 * 上传
 * @param {*} url 接口地址
 * @param {*} params 参数
 * @param {*} values 请求体
 * @returns response
 */
export const upload = (url = '', params = {}, values = {}, isLoading = true) => request({
	url,
	method: 'POST',
	params,
	data: values,
	isUpload: true,
});

/**
 * 上传-修改
 * @param {*} url 接口地址
 * @param {*} params 参数
 * @param {*} values 请求体
 * @returns response
 */
export const uploadModify = (url = '', params = {}, values = {}) => request({
	url,
	method: 'PUT',
	params,
	data: values,
	isUpload: true,
});

/**
 * 查询附件
 * @param {*} values 请求体
 * @param {*} params 请求参数
 * @returns response
 */
export const getUploadFiles = (params = {}, values = {}) => request({
	url: 'attach/query',
	method: 'POST',
	params: { ...params, ...{ isHideMessage: true } }, // 隐藏消息提示
	data: values,
});
