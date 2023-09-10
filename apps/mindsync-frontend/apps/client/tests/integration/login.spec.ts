import { expect, Page, test } from '@playwright/test';
import { createMockAccessToken } from '../unit/AccessTokenMocks';
import { createMockUser } from '../unit/UserMocks';

async function mockXsrfRoute(
  page: Page,
  status: number = 200,
  xsrfToken?: string = '8fc494c9-44ff-4517-a8ef-9494f43fac72'
) {
  await page.route('*/**/api/check-health', (route) => {
    route.fulfill({
      status: status,
      headers: {
        'Set-Cookie': `XSRF-TOKEN=${xsrfToken}; Path=/; HttpOnly; SameSite=Lax; Max-Age=3600`,
      },
      body: 'OK',
    });
  });
}

async function mockLoginRoute<T>(
  page: Page,
  status: number = 200,
  responseData: T = createMockAccessToken()
) {
  await page.route('*/**/api/login', (route) => {
    route.fulfill({
      status: status,
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(responseData),
    });
  });
}

async function mockAccountRoute<T>(
  page: Page,
  status: number = 200,
  responseData: T = createMockUser()
) {
  await page.route('*/**/api/account', (route) => {
    route.fulfill({
      status: status,
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(responseData),
    });
  });
}

test('login a user in the platform', async ({ page }) => {
  await mockXsrfRoute(page);
  await mockLoginRoute(page);
  await mockAccountRoute(page);

  await page.goto('http://localhost:5173/login');
  expect(await page.title()).toBe('MindSync');
  const emailInput = await page.locator('[id="email"]');
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
  await page.waitForNavigation();
  expect(await page.title()).toBe('MindSync');
  // await to redirect to home page after login
  expect(await page.url()).toBe('http://localhost:5173/');
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
  const emailInput = await page.locator('[id="email"]');
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
  await page.waitForNavigation();
  expect(await page.title()).toBe('MindSync');
  // the user should be in the login page
  expect(await page.url()).toBe('http://localhost:5173/forbidden');
  // after 3 seconds the user should be redirected to the login page again
  await page.waitForTimeout(3000);
  expect(await page.url()).toBe('http://localhost:5173/login');
});
