const sessionCache = {
  set(key: string, value: string) {
    if (!sessionStorage) {
      return;
    }

    sessionStorage.setItem(key, value);
  },
  get(key: string) {
    if (!sessionStorage) {
      return null;
    }

    return sessionStorage.getItem(key);
  },
  setJSON(key: string, jsonValue: Record<string, unknown> | any[]) {
    this.set(key, JSON.stringify(jsonValue));
  },
  getJSON(key: string) {
    const value = this.get(key);
    if (value !== null) {
      return JSON.parse(value) as Record<string, unknown> | any[];
    }

    return null;
  },
  remove(key: string) {
    sessionStorage.removeItem(key);
  },
};
const localCache = {
  set(key: string, value: string) {
    if (!localStorage) {
      return;
    }

    localStorage.setItem(key, value);
  },
  get(key: string) {
    if (!localStorage) {
      return null;
    }

    return localStorage.getItem(key);
  },
  setJSON(key: string, jsonValue: Record<string, unknown>) {
    this.set(key, JSON.stringify(jsonValue));
  },
  getJSON(key: string) {
    const value = this.get(key);
    if (value !== null) {
      return JSON.parse(value) as Record<string, unknown>;
    }

    return null;
  },
  remove(key: string) {
    localStorage.removeItem(key);
  },
};

const expose = {
  /**
   * 会话级缓存
   */
  session: sessionCache,
  /**
   * 本地缓存
   */
  local: localCache,
};

export default expose;
