// 时间格式转换

// const dayjs = require('dayjs');
import dayjs from 'dayjs';

export const getDay = (times, format='YYYY-MM-DD HH:mm:ss') => {
	if (!times) return '-';
	if (typeof times === 'string' && times.indexOf('T') !== -1 && times.indexOf('Z') === -1) {
		return dayjs(`${times}Z`).format(format)
	}
  return dayjs(times).format(format)
};

export default {
  getDay
}
