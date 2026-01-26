import CryptoJS from 'crypto-js';

function generateRandomString() {
  const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  let result = '';
  const charactersLength = characters.length;
  for (let i = 0; i < 32; i++) {
    result += characters.charAt(Math.floor(Math.random() * charactersLength));
  }

  return result;
}

export function generateAesKey() {
  return CryptoJS.enc.Utf8.parse(generateRandomString());
}

export function encryptBase64(string_: CryptoJS.lib.WordArray) {
  return CryptoJS.enc.Base64.stringify(string_);
}

export function decryptBase64(string_: string) {
  return CryptoJS.enc.Base64.parse(string_);
}

export function encryptWithAes(message: string, aesKey: CryptoJS.lib.WordArray) {
  const encrypted = CryptoJS.AES.encrypt(message, aesKey, {
    mode: CryptoJS.mode.ECB,
    padding: CryptoJS.pad.Pkcs7,
  });
  return encrypted.toString();
}

export function decryptWithAes(message: string, aesKey: CryptoJS.lib.WordArray) {
  const decrypted = CryptoJS.AES.decrypt(message, aesKey, {
    mode: CryptoJS.mode.ECB,
    padding: CryptoJS.pad.Pkcs7,
  });
  return decrypted.toString(CryptoJS.enc.Utf8);
}
