import { expect, test } from '@playwright/test';
import {
  mockAccountRoute,
  mockLoginRoute,
  mockXsrfRoute,
} from './mockAccountRoute';

test('should logout a user in the platform', async ({ page }) => {
  await mockXsrfRoute(page);
  await mockLoginRoute(page);
  await mockAccountRoute(page);

  await page.goto('http://localhost:5173/');
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
  expect(await page.title()).toBe('MindSync');
  await page.waitForURL('http://localhost:5173/');
  expect(page.url()).toBe('http://localhost:5173/');

  // await for the user menu to be visible in the page header and click on it
  await page.waitForSelector('[data-test-id="user-menu-button"]');
  await page.locator('[data-test-id="user-menu-button"]').click();
  await page.waitForSelector('[data-test-id="logout-button"]');
  await page.locator('[data-test-id="logout-button"]').click();

  await page.waitForURL('http://localhost:5173/login');
  expect(page.url()).toBe('http://localhost:5173/login');
});
