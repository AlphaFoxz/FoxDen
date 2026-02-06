/**
 * 路径匹配器
 * @param {string} pattern
 * @param {string} path
 * @returns {Boolean}
 */
export function isPathMatch(pattern: string, path: string) {
  const regexPattern = pattern
    .replaceAll('/', String.raw`\/`)
    .replaceAll('**', '__DOUBLE_STAR__')
    .replaceAll('*', String.raw`[^\/]*`)
    .replaceAll('__DOUBLE_STAR__', '.*');
  const regex = new RegExp(`^${regexPattern}$`);
  return regex.test(path);
}

/**
 * 判断url是否是http或https
 * @returns {Boolean}
 * @param url
 */
export const isHttp = (url: string): boolean => url.includes('http://') || url.includes('https://');

/**
 * 判断path是否为外链
 * @param {string} path
 * @returns {Boolean}
 */
export const isExternal = (path: string) => /^(https?:|mailto:|tel:)/.test(path);

/**
 * @param {string} str
 * @returns {Boolean}
 */
export const validUsername = (string_: string) => {
  const valid_map = ['admin', 'editor'];
  return valid_map.includes(string_.trim());
};

/**
 * @param {string} url
 * @returns {Boolean}
 */
export const validURL = (url: string) => {
  const reg
    = /^(https?|ftp):\/\/([a-zA-Z\d.-]+(:[a-zA-Z\d.&%$-]+)*@)*((25[0-5]|2[0-4]\d|1\d{2}|[1-9]\d?)(\.(25[0-5]|2[0-4]\d|1\d{2}|[1-9]?\d)){3}|([a-zA-Z\d-]+\.)*[a-zA-Z\d-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(:\d+)*(\/($|[\w.,?'\\+&%$#=~-]+))*$/;
  return reg.test(url);
};

/**
 * @param {string} str
 * @returns {Boolean}
 */
export const validLowerCase = (string_: string) => {
  const reg = /^[a-z]+$/;
  return reg.test(string_);
};

/**
 * @param {string} str
 * @returns {Boolean}
 */
export const validUpperCase = (string_: string) => {
  const reg = /^[A-Z]+$/;
  return reg.test(string_);
};

/**
 * @param {string} str
 * @returns {Boolean}
 */
export const validAlphabets = (string_: string) => {
  const reg = /^[A-Za-z]+$/;
  return reg.test(string_);
};

/**
 * @param {string} email
 * @returns {Boolean}
 */
export const validEmail = (email: string) => {
  const reg
    = /^(([^<>()\]\\.,;:\s@"]+(\.[^<>()\]\\.,;:\s@"]+)*)|(".+"))@((\[(?:\d{1,3}\.){3}\d{1,3}])|(([a-zA-Z\-\d]+\.)+[a-zA-Z]{2,}))$/;
  return reg.test(email);
};

/**
 * @param {string} str
 * @returns {Boolean}
 */
export const isString = (string_: any) => typeof string_ === 'string' || string_ instanceof String;

/**
 * @param {Array} arg
 * @returns {Boolean}
 */
export const isArray = (arg: string | string[]) => {
  if (Array.isArray === undefined) {
    return Object.prototype.toString.call(arg) === '[object Array]';
  }

  return Array.isArray(arg);
};
