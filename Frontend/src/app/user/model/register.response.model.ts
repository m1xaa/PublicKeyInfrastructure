import { UserRole } from '../../infrastructure/auth/model/user-role.model';

export interface RegisterResponse {
  email: string;
  firstName: string;
  lastName: string;
  userRole: UserRole;
}
