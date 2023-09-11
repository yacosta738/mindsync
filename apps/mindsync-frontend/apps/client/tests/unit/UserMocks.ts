import { expect } from 'vitest';
import User from '../../src/authentication/domain/User';
import { Authority } from '../../src/authentication/domain/Authority';

export const compareUserAttributes = (
  primaryUser: User,
  comparisonUser: User = {
    id: 'test',
    username: 'test',
    email: 'test@gmail.com',
    firstname: 'test',
    lastname: 'test',
    authorities: [Authority.USER],
  }
) => {
  expect(primaryUser).toBeDefined();
  expect(primaryUser?.id).toBe(comparisonUser.id);
  expect(primaryUser?.username).toBe(comparisonUser.username);
  expect(primaryUser?.email).toBe(comparisonUser.email);
  expect(primaryUser?.firstname).toBe(comparisonUser.firstname);
  expect(primaryUser?.lastname).toBe(comparisonUser.lastname);
  expect(primaryUser?.authorities).toBeDefined();
  expect(primaryUser?.authorities.length).toBe(
    comparisonUser.authorities.length
  );
  primaryUser.authorities.forEach((authority, index) => {
    expect(authority).toBe(comparisonUser.authorities[index]);
  });
};

export const createMockUser = (
  id: string = 'test',
  username: string = 'test',
  email: string = 'test@gmail.com',
  firstname: string = 'test',
  lastname: string = 'test',
  authorities: Authority[] = [Authority.USER]
): User => {
  return { id, username, email, firstname, lastname, authorities };
};
