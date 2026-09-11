import {defineConfig} from '@playwright/test';
export default defineConfig({
  testDir: './tests',
  use: {baseURL: 'http://127.0.0.1:8072', headless: true},
  webServer: {command: 'python3 -m http.server 8072 --bind 127.0.0.1 --directory public',
              url: 'http://127.0.0.1:8072', reuseExistingServer: false}
});
