import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map } from 'rxjs';
import { AuthService } from '../../service/auth.service';

export const authGuard: CanActivateFn = (_route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.loggedIn$.pipe(
    map((loggedIn) => {
      if (!loggedIn) {
        router.navigate(['/user/login'], {
          queryParams: { returnUrl: state.url },
        });
      }
      return loggedIn;
    })
  );
};
