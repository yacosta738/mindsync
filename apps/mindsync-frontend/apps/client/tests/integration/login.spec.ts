import { expect, test } from '@playwright/test';
import { createMockAccessToken } from '../unit/AccessTokenMocks';
import { createMockUser } from '../unit/UserMocks';

test('login a user in the platform', async ({ page }) => {
  await page.route('*/**/api/check-health', (route) => {
    route.fulfill({
      status: 200,
      headers: {
        'Set-Cookie':
          'XSRF-TOKEN=8fc494c9-44ff-4517-a8ef-9494f43fac72; Path=/; HttpOnly; SameSite=Lax; Max-Age=3600',
      },
      body: 'OK',
    });
  });
  const accessToken = createMockAccessToken();
  await page.route('*/**/api/login', (route) => {
    route.fulfill({
      status: 200,
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(accessToken),
    });
  });
  const user = createMockUser();
  await page.route('*/**/api/account', (route) => {
    route.fulfill({
      status: 200,
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(user),
    });
  });

  await page.goto('http://localhost:5173/login');
  expect(await page.title()).toBe('MindSync');
  const emailInput = await page.locator('[id="email"]');
  expect(await emailInput.textContent()).toBe('');
  const passwordInput = page.locator('[id="password"]');
  expect(await passwordInput.textContent()).toBe('');
  // // fill email field
  await emailInput.fill('john.doe@mindsync.com');
  // // tab to password field
  await page.keyboard.press('Tab');
  // // fill password field
  await passwordInput.fill('S3cr3tP@ssw0rd*123');

  await page.getByRole('button', { name: 'Sign in' }).click();
  await page.waitForNavigation();
  expect(await page.title()).toBe('MindSync');
  // await to redirect to home page after login
  expect(await page.url()).toBe('http://localhost:5173/');
});
