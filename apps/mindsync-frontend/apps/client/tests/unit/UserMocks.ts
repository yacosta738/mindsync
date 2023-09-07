import { expect } from 'vitest';
import User from '../../src/authentication/domain/User';
import { Authority } from '../../src/authentication/domain/Authority';

export const validateUserAttributes = (user: User) => {
  expect(user).toBeDefined();
  expect(user.id).toBe('test');
  expect(user.username).toBe('test');
  expect(user.email).toBe('test@gmail.com');
  expect(user.firstName).toBe('test');
  expect(user.lastName).toBe('test');
  expect(user.authorities).toBeDefined();
  expect(user.authorities.length).toBe(1);
  expect(user.authorities[0]).toBe(Authority.USER);
};

export const createMockUser = (
  id: string = 'test',
  username: string = 'test',
  email: string = 'test@gmail.com',
  firstName: string = 'test',
  lastName: string = 'test',
  authorities: Authority[] = [Authority.USER]
): User => {
  return { id, username, email, firstName, lastName, authorities };
};
