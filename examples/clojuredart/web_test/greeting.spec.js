const {test, expect} = require('@playwright/test');

test('Flutter web greets by name and handles blank input', async ({page}) => {
  // Flutter applies accessible-input focus and editing changes on animation frames.
  const settleFrames = () => page.evaluate(() => new Promise(resolve =>
    requestAnimationFrame(() => requestAnimationFrame(resolve))));
  const errors = [];
  page.on('pageerror', error => errors.push(error.message));
  await page.goto('/');
  // Flutter exposes this control for enabling its accessible DOM representation.
  await page.locator('flt-semantics-placeholder').evaluate(element => element.click());
  const name = page.getByRole('textbox', {name: /Your name/});
  await expect(page.locator('flt-semantics span').getByText('Hello, world!', {exact: true})).toBeVisible();
  await name.click();
  await settleFrames();
  await name.fill('  Björk 🌍  ');
  await settleFrames();
  await expect(name).toHaveValue('  Björk 🌍  ');
  await page.getByRole('button', {name: 'Say hello'}).click();
  await expect(page.locator('flt-semantics span').getByText('Hello, Björk 🌍!', {exact: true})).toBeVisible();
  await expect(page.getByText('Greetings sent: 1', {exact: true})).toBeVisible();
  await page.screenshot({path: 'screenshot-web.png'});
  await name.click();
  await settleFrames();
  await name.fill('   ');
  await settleFrames();
  await expect(name).toHaveValue('   ');
  await name.press('Enter');
  await expect(page.locator('flt-semantics span').getByText('Hello, world!', {exact: true})).toBeVisible();
  await expect(page.getByText('Greetings sent: 2', {exact: true})).toBeVisible();
  expect(errors).toEqual([]);
});
