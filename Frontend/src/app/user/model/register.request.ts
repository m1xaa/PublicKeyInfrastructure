import { UserRole } from '../../infrastructure/auth/model/user-role.model';

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  organizationId: number;
  email: string;
  password: string;
  userRole: UserRole;
}
