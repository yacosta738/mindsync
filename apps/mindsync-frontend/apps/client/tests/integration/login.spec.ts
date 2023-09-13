import { expect, test } from '@playwright/test';
import {
  mockAccountRoute,
  mockLoginRoute,
  mockXsrfRoute,
} from './mockAccountRoute';

test('login a user in the platform', async ({ page }) => {
  await mockXsrfRoute(page);
  await mockLoginRoute(page);
  await mockAccountRoute(page);

  await page.goto('http://localhost:5173/login');
  expect(await page.title()).toBe('MindSync');
  const emailInput = page.locator('[id="email"]');
  expect(await emailInput.textContent()).toBe('');
  const passwordInput = page.locator('[id="password"]');
  expect(await passwordInput.textContent()).toBe('');
  // fill email field
  await emailInput.fill('john.doe@mindsync.com');
  // tab to password field
  await page.keyboard.press('Tab');
  // fill password field
  await passwordInput.fill('S3cr3tP@ssw0rd*123');

  await page.getByRole('button', { name: 'Sign in' }).click();
  await page.waitForURL('http://localhost:5173/');
  expect(await page.title()).toBe('MindSync');
  // await to redirect to home page after login
  expect(page.url()).toBe('http://localhost:5173/');
});

test('should fail when login a user in the platform with wrong credentials', async ({
  page,
}) => {
  await mockXsrfRoute(page);
  await mockLoginRoute(page, 401, {
    message: 'Invalid credentials',
  });
  await page.goto('http://localhost:5173/login');
  expect(await page.title()).toBe('MindSync');
  const emailInput = page.locator('[id="email"]');
  expect(await emailInput.textContent()).toBe('');
  const passwordInput = page.locator('[id="password"]');
  expect(await passwordInput.textContent()).toBe('');
  // fill email field
  await emailInput.fill('john.doe@mindsync.com');
  // tab to password field
  await page.keyboard.press('Tab');
  // fill password field
  await passwordInput.fill('S3cr3tP@ssw0rd*123*Wr0ng');

  await page.getByRole('button', { name: 'Sign in' }).click();
  expect(await page.title()).toBe('MindSync');
  // the user should be in the login page
  expect(page.url()).toBe('http://localhost:5173/login');
});
