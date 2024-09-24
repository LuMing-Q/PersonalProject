export const getUser = () => {
    const userString = sessionStorage.getItem('userInfo');
    if (userString) return JSON.parse(userString);
    return null;
  };
  export const clearUser = () => {
    sessionStorage.removeItem('userInfo');
  };
  
  export const setUser = (user) => {
    sessionStorage.setItem('userInfo', JSON.stringify(user));
  };
  
  export const setStorage = (key, values) => {
    sessionStorage.setItem(key, JSON.stringify(values));
  };
  
  export const getStorage = (key) => {
    const value = sessionStorage.getItem(key);
    if (value) return JSON.parse(value);
    return null;
  };
  
  export const clearStorage = (key) => {
    if (key && key.length && typeof key === 'string') {
      sessionStorage.removeItem(key);
    } else {
      console.error('sessionStorage.removeItem(key):  key错误');
    }
  };
  
  export const clearStorageAll = () => sessionStorage.clear();
  
  export default {
    setUser, getUser, clearUser, setStorage, getStorage, clearStorage, clearStorageAll,
  };
  