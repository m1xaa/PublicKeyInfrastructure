import { UserRole } from '../../infrastructure/auth/model/user-role.model';

export interface RegisterCARequest {
  firstName: string;
  lastName: string;
  organizationId: string;
  email: string;
  userRole: UserRole;
}
