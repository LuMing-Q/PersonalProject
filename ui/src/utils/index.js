import CryptoJs from 'crypto-js';

// 常用工具函数
import session from './session.js';
import request from './request.js';
import { buildTree } from './tree.js';
import day from './day.js';
import echartsFit from './echartsFit.js';

// 转换ob类型的数据
const parseValues = (values) => JSON.parse(JSON.stringify(values));

const aesKey = '21CMWDOSEilckmyl';
const encrypt = (str = '') => {
  let key = CryptoJs.enc.Utf8.parse(aesKey);
  let srcs = CryptoJs.enc.Utf8.parse(str);
  let encrypted = CryptoJs.AES.encrypt(srcs, key, {
    mode: CryptoJs.mode.ECB,
    padding: CryptoJs.pad.Pkcs7,
  });
  return encrypted.toString();
};

export  {
	buildTree,
	request,
	session,
  parseValues,
	day,
	encrypt,
	echartsFit
}
