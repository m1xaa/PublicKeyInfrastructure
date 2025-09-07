import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map } from 'rxjs';
import { AuthService } from '../../service/auth.service';

export const unauthenticatedGuard: CanActivateFn = (_route, _state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.loggedIn$.pipe(
    map((loggedIn) => {
      if (loggedIn) {
        router.navigateByUrl('/');
      }
      return !loggedIn;
    })
  );
};
