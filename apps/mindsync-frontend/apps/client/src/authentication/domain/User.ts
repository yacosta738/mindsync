import type { Authority } from './Authority';

export default class User {
  constructor(
    public id: string,
    public username: string,
    public email: string,
    public firstName: string,
    public lastName: string,
    public authorities: Authority[]
  ) {}
}
