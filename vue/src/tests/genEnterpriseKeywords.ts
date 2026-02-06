import Bun from 'bun';

const file = Bun.file('D:/Desktop/ent.txt');
const key = 'app-hCz4AnxNY3thdzAnEjofAuch';
const baseUrl = 'http://115.190.23.242:30080/v1';

const form = new FormData();
form.append('file', file);
form.append('user', 'admin');
let response = await fetch(baseUrl + '/files/upload', {
  method: 'POST',
  headers: {
    Authorization: 'Bearer ' + key,
    'Content-Type': 'multipart/form-data',
  },
  body: form,
});
if (response.status !== 200) {
  console.error(await response.text());
  throw new Error('上传文件失败');
}

const resBody = await response.json();
// Const fileId = f04f381a-f3f6-4a74-9ab4-ab80e22a4fb3
const fileId = resBody.id;
console.log('文件id', fileId);

response = await fetch(baseUrl + '/workflows/run', {
  method: 'post',
  headers: {
    Authorization: 'Bearer ' + key,
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    responseMode: 'blocking',
    user: 'admin',
    inputs: {
      company_list_json: [
        {
          type: 'document',
          transfer_method: 'local_file',
          upload_file_id: fileId,
        },
      ],
      // 'policy': 'a616a494-8f04-49a5-ac20-fed47ddae584'
      // 'policy': '770f1ac1-c1d1-4e88-b1c7-40f9308f8d8c'
    },
  }),
});

const res = await response.json();
console.log('获取关键字', res);
