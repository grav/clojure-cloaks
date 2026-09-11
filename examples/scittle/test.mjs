import assert from 'node:assert/strict';
import {pathToFileURL} from 'node:url';
import {resolve} from 'node:path';
import {chromium} from 'playwright';

const browser = await chromium.launch({headless:true,
  ...(process.env.CHROMIUM_PATH ? {executablePath:process.env.CHROMIUM_PATH} : {})});
try {
  const page = await browser.newPage({viewport:{width:640,height:240}});
  const errors = [];
  page.on('pageerror', e => errors.push(e.message));
  await page.goto(pathToFileURL(resolve('index.html')).href);
  assert.equal(await page.getByRole('status').textContent(), 'Hello, world!');
  for (let n=1; n<=3; n++) {
    await page.getByRole('button', {name:'Say hello'}).click();
    assert.equal(await page.getByRole('status').textContent(), `Hello from Scittle! Click #${n}`);
  }
  assert.deepEqual(errors, []);
  await page.screenshot({path:'screenshot.png'});
  console.log('PASS: file:// loading and three clicks update the greeting through Scittle.');
} finally {
  await browser.close();
}
