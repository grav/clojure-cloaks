import {test, expect} from '@playwright/test';

const appURL = new URL('../public/index.html', import.meta.url).href;

test('greet, navigate without reloading, clear, and handle literal HTML safely', async ({page}) => {
  const errors = [];
  page.on('pageerror', error => errors.push(error.message));
  await page.goto(appURL);
  await expect(page.getByRole('status')).toHaveText('Hello, world!');
  await page.getByLabel("What's your name?").fill('Ada');
  await page.getByRole('button', {name: 'Say hello'}).click();
  await expect(page.getByRole('status')).toHaveText('Hello, Ada!');
  await page.getByLabel("What's your name?").fill('<b>world</b>');
  await page.getByRole('button', {name: 'Say hello'}).click();
  await expect(page.getByRole('status')).toHaveText('Hello, <b>world</b>!');
  await expect(page.locator('[role=status] b')).toHaveCount(0);
  await page.getByRole('link', {name: 'Greetings (2)'}).click();
  await expect(page.getByRole('listitem')).toHaveText(['Hello, Ada!', 'Hello, <b>world</b>!']);
  await page.goBack();
  await expect(page.getByLabel("What's your name?")).toHaveValue('<b>world</b>');
  await page.getByLabel("What's your name?").fill('   ');
  await page.getByRole('button', {name: 'Say hello'}).click();
  await expect(page.getByRole('status')).toHaveText('Hello, world!');
  await page.getByRole('link', {name: 'Greetings (3)'}).click();
  await page.getByRole('button', {name: 'Clear greetings'}).click();
  await expect(page.getByText('No greetings yet. Say hello to get started.')).toBeVisible();
  expect(errors).toEqual([]);
  await page.getByRole('link', {name: 'Say hello'}).click();
  await page.getByLabel("What's your name?").fill('Clojure');
  await page.getByRole('button', {name: 'Say hello'}).click();
  await page.screenshot({path: 'screenshot.png', fullPage: true});
});
