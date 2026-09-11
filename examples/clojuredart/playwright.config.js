const {defineConfig} = require('@playwright/test');
module.exports = defineConfig({
  testDir: './web_test',
  use: {baseURL: 'http://127.0.0.1:8074', headless: true, viewport: {width: 1000, height: 800}},
  webServer: {command: 'python3 -m http.server 8074 --bind 127.0.0.1 --directory build/web',
              url: 'http://127.0.0.1:8074', reuseExistingServer: false}
});
