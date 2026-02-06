import Bun from 'bun';

const uploadFileId = 'b74a00ba-362d-4de5-958f-e178dbc3e10a'; // 最新
// const uploadFileId = 'c362da58-4979-4cbf-80bf-56a03788166e';

const response = await fetch('http://115.190.23.242:30080/v1/workflows/run', {
  method: 'POST',
  headers: {
    Authorization: 'Bearer app-RGewLbGppYaIsOx7yZlEqFSe',
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    responseMode: 'blocking',
    user: 'admin',
    inputs: {
      company_list_json: {
        type: 'document',
        transfer_method: 'local_file',
        upload_file_id: uploadFileId,
      },
      // 'policy': 'a616a494-8f04-49a5-ac20-fed47ddae584'
      policy: '770f1ac1-c1d1-4e88-b1c7-40f9308f8d8c',
    },
  }),
});

const file = Bun.file('C:/Users/Wong/Desktop/cache-4728582618394324303.txt');
/*
Const form = new FormData();
form.append('responseMode', 'blocking');
form.append('user', 'admin');
form.append('company_list_json', file);
form.append('policy', 'a616a494-8f04-49a5-ac20-fed47ddae584');
const response = await fetch('http://115.190.23.242:30080/v1/workflows/run', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer app-RGewLbGppYaIsOx7yZlEqFSe',
    'Content-Type': 'multipart/form-data'
  },
  body: form
});
 */
console.log(JSON.stringify(await response.json(), null, 2));
