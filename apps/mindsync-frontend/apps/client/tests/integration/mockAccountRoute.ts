import { Page } from '@playwright/test';
import { createMockAccessToken } from '../unit/AccessTokenMocks';
import { createMockUser } from '../unit/UserMocks';

export async function mockXsrfRoute(
  page: Page,
  status: number = 200,
  xsrfToken: string = '8fc494c9-44ff-4517-a8ef-9494f43fac72'
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

export async function mockLoginRoute<T>(
  page: Page,
  status: number = 200,
  responseData: T = createMockAccessToken() as unknown as T
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

export async function mockAccountRoute<T>(
  page: Page,
  status: number = 200,
  responseData: T = createMockUser() as unknown as T
) {
  await page.route('**/api/account', (route) => {
    route.fulfill({
      status: status,
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(responseData),
    });
  });
}
