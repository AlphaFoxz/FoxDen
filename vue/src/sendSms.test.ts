// Import fetch from 'node:fetch';

fetch('https://wosdk.028lk.com:7072/Api/SendSms', {
  method: 'post',
  body: JSON.stringify({
    phone: 'xxx',
  }),
});
