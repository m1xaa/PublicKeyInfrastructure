import { UserRole } from '../../infrastructure/auth/model/user-role.model';

export interface LoginResponse {
  userId: number;
  email: string;
  firstName: string;
  lastName: string;
  role: UserRole;
  jwt: string;
  refreshToken: string;
}
