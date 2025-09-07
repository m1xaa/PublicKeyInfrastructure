import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map } from 'rxjs';
import { UserRole } from '../model/user-role.model';
import { AuthService } from '../../service/auth.service';

export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.userRole$.pipe(
    map((userRole) => {
      const includesRole = route.data['roles'].includes(userRole);
      if (userRole === UserRole.Unauthenticated && !includesRole) {
        router.navigate(['/user/login'], {
          queryParams: { returnUrl: state.url },
        });
      }
      return includesRole;
    })
  );
};
