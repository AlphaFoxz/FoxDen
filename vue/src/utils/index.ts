import {parseTime} from '@/utils/ruoyi';

/**
 * 表格时间格式化
 */
export const formatDate = (cellValue: string) => {
  if (cellValue == null || cellValue == '') {
    return '';
  }

  const date = new Date(cellValue);
  const year = date.getFullYear();
  const month = date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1;
  const day = date.getDate() < 10 ? '0' + date.getDate() : date.getDate();
  const hours = date.getHours() < 10 ? '0' + date.getHours() : date.getHours();
  const minutes = date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes();
  const seconds = date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds();
  return year + '-' + month + '-' + day + ' ' + hours + ':' + minutes + ':' + seconds;
};

/**
 * @param {number} time
 * @param {string} option
 * @returns {string}
 */
export const formatTime = (time: string, option: string) => {
  let t: number;
  t = ('' + time).length === 10 ? Number.parseInt(time) * 1000 : +time;
  const d: any = new Date(t);
  const now = Date.now();

  const diff = (now - d) / 1000;

  if (diff < 30) {
    return '刚刚';
  }

  if (diff < 3600) {
    // Less 1 hour
    return Math.ceil(diff / 60) + '分钟前';
  }

  if (diff < 3600 * 24) {
    return Math.ceil(diff / 3600) + '小时前';
  }

  if (diff < 3600 * 24 * 2) {
    return '1天前';
  }

  if (option) {
    return parseTime(t, option);
  }

  return d.getMonth() + 1 + '月' + d.getDate() + '日' + d.getHours() + '时' + d.getMinutes() + '分';
};

/**
 * @param {string} url
 * @returns {Object}
 */
export const getQueryObject = (url: string) => {
  url = url == null ? globalThis.location.href : url;
  const search = url.slice(Math.max(0, url.lastIndexOf('?') + 1));
  const object: Record<string, string> = {};
  const reg = /([^?&=]+)=([^?&=]*)/g;
  search.replaceAll(reg, (rs, $1, $2) => {
    const name = decodeURIComponent($1);
    let value = decodeURIComponent($2);
    value = String(value);
    object[name] = value;
    return rs;
  });
  return object;
};

/**
 * @param {string} input value
 * @returns {number} output value
 */
export const byteLength = (string_: string) => {
  // Returns the byte length of an utf8 string
  let s = string_.length;
  for (let i = string_.length - 1; i >= 0; i--) {
    const code = string_.charCodeAt(i);
    if (code > 0x7F && code <= 0x7_FF) {
      s++;
    } else if (code > 0x7_FF && code <= 0xFF_FF) {
      s += 2;
    }

    if (code >= 0xDC_00 && code <= 0xDF_FF) {
      i--;
    }
  }

  return s;
};

/**
 * @param {Array} actual
 * @returns {Array}
 */
export const cleanArray = (actual: any[]) => {
  const newArray: any[] = [];
  for (const element of actual) {
    if (element) {
      newArray.push(element);
    }
  }

  return newArray;
};

/**
 * @param {Object} json
 * @returns {Array}
 */
export const param = (json: any) => {
  if (!json) {
    return '';
  }

  return cleanArray(Object.keys(json).map(key => {
    if (json[key] === undefined) {
      return '';
    }

    return encodeURIComponent(key) + '=' + encodeURIComponent(json[key]);
  })).join('&');
};

/**
 * @param {string} url
 * @returns {Object}
 */
export const param2Obj = (url: string) => {
  const search = decodeURIComponent(url.split('?')[1]).replaceAll('+', ' ');
  if (!search) {
    return {};
  }

  const object: any = {};
  const searchArray = search.split('&');
  for (const v of searchArray) {
    const index = v.indexOf('=');
    if (index !== -1) {
      const name = v.slice(0, Math.max(0, index));
      const value = v.substring(index + 1, v.length);
      object[name] = value;
    }
  }

  return object;
};

/**
 * @param {string} val
 * @returns {string}
 */
export const html2Text = (value: string) => {
  const div = document.createElement('div');
  div.innerHTML = value;
  return div.textContent || div.innerText;
};

/**
 * Merges two objects, giving the last one precedence
 * @param {Object} target
 * @param {(Object|Array)} source
 * @returns {Object}
 */
export const objectMerge = (target: any, source: any | any[]) => {
  if (typeof target !== 'object') {
    target = {};
  }

  if (Array.isArray(source)) {
    return [...source];
  }

  for (const property of Object.keys(source)) {
    const sourceProperty = source[property];
    target[property] = typeof sourceProperty === 'object' ? objectMerge(target[property], sourceProperty) : sourceProperty;
  }

  return target;
};

/**
 * @param {HTMLElement} element
 * @param {string} className
 */
export const toggleClass = (element: HTMLElement, className: string) => {
  if (!element || !className) {
    return;
  }

  let classString = element.className;
  const nameIndex = classString.indexOf(className);
  if (nameIndex === -1) {
    classString += '' + className;
  } else {
    classString = classString.slice(0, Math.max(0, nameIndex)) + classString.slice(Math.max(0, nameIndex + className.length));
  }

  element.className = classString;
};

/**
 * @param {string} type
 * @returns {Date}
 */
export const getTime = (type: string) => {
  if (type === 'start') {
    return Date.now() - 3600 * 1000 * 24 * 90;
  }

  return new Date(new Date().toDateString());
};

/**
 * @param {Function} func
 * @param {number} wait
 * @param {boolean} immediate
 * @return {*}
 */
export const debounce = (func: any, wait: number, immediate: boolean) => {
  let timeout: any; let args: any; let context: any; let timestamp: any; let result: any;

  const later = function () {
    // 据上一次触发时间间隔
    const last = Date.now() - timestamp;

    // 上次被包装函数被调用时间间隔 last 小于设定时间间隔 wait
    if (last < wait && last > 0) {
      timeout = setTimeout(later, wait - last);
    } else {
      timeout = null;
      // 如果设定为immediate===true，因为开始边界已经调用过了此处无需调用
      if (!immediate) {
        result = func.apply(context, args);
        if (!timeout) {
          context = args = null;
        }
      }
    }
  };

  return (...args: any) => {
    context = this;
    timestamp = Date.now();
    const callNow = immediate && !timeout;
    // 如果延时不存在，重新设定延时
    timeout ||= setTimeout(later, wait);
    if (callNow) {
      result = func.apply(context, args);
      context = args = null;
    }

    return result;
  };
};

/**
 * This is just a simple version of deep copy
 * Has a lot of edge cases bug
 * If you want to use a perfect deep copy, use lodash's _.cloneDeep
 * @param {Object} source
 * @returns {Object}
 */
export const deepClone = (source: any) => {
  if (!source && typeof source !== 'object') {
    throw new Error('error arguments', 'deepClone' as any);
  }

  const targetObject: any = source.constructor === Array ? [] : {};
  for (const keys of Object.keys(source)) {
    targetObject[keys] = source[keys] && typeof source[keys] === 'object' ? deepClone(source[keys]) : source[keys];
  }

  return targetObject;
};

/**
 * @param {Array} arr
 * @returns {Array}
 */
export const uniqueArr = (array: any) => [...new Set(array)];

/**
 * @returns {string}
 */
export const createUniqueString = (): string => {
  const timestamp = Date.now() + '';
  const number_ = (1 + Math.random()) * 65_536;
  const randomNumber = Number.parseInt(number_ + '');
  return (+(randomNumber + timestamp)).toString(32);
};

/**
 * Check if an element has a class
 * @param ele
 * @param {string} cls
 * @returns {boolean}
 */
export const hasClass = (ele: HTMLElement, cls: string): boolean => Boolean(new RegExp(String.raw`(\s|^)` + cls + String.raw`(\s|$)`).test(ele.className));

/**
 * Add class to element
 * @param ele
 * @param {string} cls
 */
export const addClass = (ele: HTMLElement, cls: string) => {
  if (!hasClass(ele, cls)) {
    ele.className += ' ' + cls;
  }
};

/**
 * Remove class from element
 * @param ele
 * @param {string} cls
 */
export const removeClass = (ele: HTMLElement, cls: string) => {
  if (hasClass(ele, cls)) {
    const reg = new RegExp(String.raw`(\s|^)` + cls + String.raw`(\s|$)`);
    ele.className = ele.className.replace(reg, ' ');
  }
};

/**
 * @param {string} path
 * @returns {Boolean}
 */
export const isExternal = (path: string) => /^(https?:|http?:|mailto:|tel:)/.test(path);
