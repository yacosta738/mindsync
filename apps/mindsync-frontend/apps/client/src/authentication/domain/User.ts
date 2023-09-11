import type { Authority } from './Authority';

export default class User {
  constructor(
    public id: string,
    public username: string,
    public email: string,
    public firstname: string,
    public lastname: string,
    public authorities: Authority[]
  ) {}
}
