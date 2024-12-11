// 时间格式转换
import dayjs from 'dayjs';

export const getDay = (times, format = 'YYYY-MM-DD HH:mm:ss') => {
  if (!times) return '-';
  if (typeof times === 'string' && times.includes('T') &&!times.includes('Z')) {
    return dayjs(`${times}Z`).format(format);
  }
  return dayjs(times).format(format);
};

export default {
  getDay
}
