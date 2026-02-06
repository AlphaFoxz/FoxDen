// 日期格式化
export function parseTime(time: any, pattern?: string) {
  if (arguments.length === 0 || !time) {
    return null;
  }

  const format = pattern || '{y}-{m}-{d} {h}:{i}:{s}';
  let date;
  if (typeof time === 'object') {
    date = time;
  } else {
    if (typeof time === 'string' && /^\d+$/.test(time)) {
      time = Number.parseInt(time);
    } else if (typeof time === 'string') {
      time = time
        .replaceAll(new RegExp(/-/gm), '/')
        .replace('T', ' ')
        .replaceAll(new RegExp(/\.\d{3}/gm), '');
    }

    if (typeof time === 'number' && time.toString().length === 10) {
      time *= 1000;
    }

    date = new Date(time);
  }

  const formatObject: Record<string, any> = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay(),
  };
  return format.replaceAll(/{([adhimsy])+}/g, (result: string, key: string) => {
    let value = formatObject[key];
    // Note: getDay() returns 0 on Sunday
    if (key === 'a') {
      return ['日', '一', '二', '三', '四', '五', '六'][value];
    }

    if (result.length > 0 && value < 10) {
      value = '0' + value;
    }

    return value || 0;
  });
}

/**
 * 添加日期范围
 * @param params
 * @param dateRange
 * @param propName
 */
export const addDateRange = (parameters: any, dateRange: any[], propName?: string) => {
  const search = parameters;
  search.params = typeof search.params === 'object' && search.params !== null && !Array.isArray(search.params) ? search.params : {};
  dateRange = Array.isArray(dateRange) ? dateRange : [];
  if (propName === undefined) {
    search.params.beginTime = dateRange[0];
    search.params.endTime = dateRange[1];
  } else {
    search.params['begin' + propName] = dateRange[0];
    search.params['end' + propName] = dateRange[1];
  }

  return search;
};

// 回显数据字典
export const selectDictLabel = (datas: any, value: number | string) => {
  if (value === undefined) {
    return '';
  }

  const actions: Array<string | number> = [];
  Object.keys(datas).some(key => {
    if (datas[key].value == '' + value) {
      actions.push(datas[key].label);
      return true;
    }
  });
  if (actions.length === 0) {
    actions.push(value);
  }

  return actions.join('');
};

// 回显数据字典（字符串数组）
export const selectDictLabels = (datas: any, value: any, separator: any) => {
  if (value === undefined || value.length === 0) {
    return '';
  }

  if (Array.isArray(value)) {
    value = value.join(',');
  }

  const actions: any[] = [];
  const currentSeparator = undefined === separator ? ',' : separator;
  const temporary = value.split(currentSeparator);
  Object.keys(value.split(currentSeparator)).some(value_ => {
    let match = false;
    Object.keys(datas).some(key => {
      if (datas[key].value == '' + temporary[value_]) {
        actions.push(datas[key].label + currentSeparator);
        match = true;
      }
    });
    if (!match) {
      actions.push(temporary[value_] + currentSeparator);
    }
  });
  return actions.join('').slice(0, Math.max(0, actions.join('').length - 1));
};

// 字符串格式化(%s )
export function sprintf(string_: string) {
  if (arguments.length > 0) {
    let flag = true;
    let i = 1;
    string_ = string_.replaceAll('%s', function () {
      const arg = arguments[i++];
      if (arg === undefined) {
        flag = false;
        return '';
      }

      return arg;
    });
    return flag ? string_ : '';
  }
}

// 转换字符串，undefined,null等转化为""
export const parseStrEmpty = (string_: any) => {
  if (!string_ || string_ == 'undefined' || string_ == 'null') {
    return '';
  }

  return string_;
};

// 数据合并
export const mergeRecursive = (source: any, target: any) => {
  for (const p in target) {
    try {
      source[p] = target[p].constructor == Object ? mergeRecursive(source[p], target[p]) : target[p];
    } catch {
      source[p] = target[p];
    }
  }

  return source;
};

/**
 * 构造树型结构数据
 * @param {*} data 数据源
 * @param {*} id id字段 默认 'id'
 * @param {*} parentId 父节点字段 默认 'parentId'
 * @param {*} children 孩子节点字段 默认 'children'
 */
export const handleTree = <T>(data: any[], id?: string, parentId?: string, children?: string): T[] => {
  const config: {
    id: string;
    parentId: string;
    childrenList: string;
  } = {
    id: id || 'id',
    parentId: parentId || 'parentId',
    childrenList: children || 'children',
  };

  const childrenListMap: any = {};
  const tree: T[] = [];
  for (const d of data) {
    const id = d[config.id];
    childrenListMap[id] = d;
    if (!d[config.childrenList]) {
      d[config.childrenList] = [];
    }
  }

  for (const d of data) {
    const parentId = d[config.parentId];
    const parentObject = childrenListMap[parentId];
    if (parentObject) {
      parentObject[config.childrenList].push(d);
    } else {
      tree.push(d);
    }
  }

  return tree;
};

/**
 * 参数处理
 * @param {*} params  参数
 */
export const tansParams = (parameters: any) => {
  let result = '';
  for (const propName of Object.keys(parameters)) {
    const value = parameters[propName];
    const part = encodeURIComponent(propName) + '=';
    if (value !== null && value !== '' && value !== undefined) {
      if (typeof value === 'object') {
        for (const key of Object.keys(value)) {
          if (value[key] !== null && value[key] !== '' && value[key] !== undefined) {
            const parameters = propName + '[' + key + ']';
            const subPart = encodeURIComponent(parameters) + '=';
            result += subPart + encodeURIComponent(value[key]) + '&';
          }
        }
      } else {
        result += part + encodeURIComponent(value) + '&';
      }
    }
  }

  return result;
};

// 返回项目路径
export const getNormalPath = (p: string): string => {
  if (p.length === 0 || !p || p === 'undefined') {
    return p;
  }

  const res = p.replace('//', '/');
  if (res.endsWith('/')) {
    return res.slice(0, -1);
  }

  return res;
};

// 验证是否为blob格式
export const blobValidate = (data: any) => data.type !== 'application/json';

export default {
  handleTree,
};
