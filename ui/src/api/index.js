/*
 * @LastEditTime: 2024-8-20
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
export const get = (url = '', params = {}) => request({ url, params });

/**
 * delete请求
 * @param url 接口地址
 * @param params 条件参数
 * @param values body
 * @returns response
 */
export const remove = (url = '', params = {}, values = {}) => request({
	url,
	method: 'DELETE',
	params,
	data: values,
});