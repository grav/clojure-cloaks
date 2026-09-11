import assert from 'node:assert/strict';
import {spawn} from 'node:child_process';
import {chromium} from 'playwright';
const server = spawn('python3', ['-m','http.server','8075','--bind','127.0.0.1'], {stdio:'ignore'});
let browser;
try {
  for (let n=0; n<50; n++) {
    try { if ((await fetch('http://127.0.0.1:8075')).ok) break; } catch {}
    await new Promise(r=>setTimeout(r,100));
  }
  browser = await chromium.launch({headless:true, ...(process.env.CHROMIUM_PATH ? {executablePath:process.env.CHROMIUM_PATH} : {})});
  const page = await browser.newPage({viewport:{width:1000,height:760}});
  const errors=[];
  page.on('pageerror', e=>errors.push(e.message));
  await page.goto('http://127.0.0.1:8075');
  await page.getByRole('status').filter({hasText:'Hello, world!'}).waitFor();
  for (const [input, expected] of [['  Björk 🌍  ','Hello, Björk 🌍!'], ['   ','Hello, world!'], ['<img src=x onerror=alert(1)>','Hello, <img src=x onerror=alert(1)>!']]) {
    await page.getByLabel('Your name').fill(input);
    await page.getByRole('button',{name:'Say hello'}).click();
    assert.equal(await page.getByRole('status').textContent(),expected);
  }
  assert.equal(await page.locator('#greeting img').count(),0);
  await page.getByLabel('Your name').fill('Clojure');
  await page.getByLabel('Your name').press('Enter');
  assert.equal(await page.getByRole('status').textContent(),'Hello, Clojure!');
  await page.screenshot({path:'screenshot.png'});
  assert.deepEqual(errors,[]);
  console.log('PASS: Scittle loads, default/Unicode/blank/literal HTML greetings, button and Enter submission; no browser errors.');
} finally {
  await browser?.close();
  server.kill();
}
