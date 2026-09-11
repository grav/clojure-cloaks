import {defineConfig} from '@playwright/test';
export default defineConfig({
  testDir: './tests',
  use: {
    headless: true,
    launchOptions: process.env.CHROMIUM_PATH
      ? {executablePath: process.env.CHROMIUM_PATH} : {}
  }
});
