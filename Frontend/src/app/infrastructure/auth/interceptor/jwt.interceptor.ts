import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../../service/auth.service';
import { RefreshTokenResponse } from '../model/refresh-token.response';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const authToken = authService.getJwt();

  if (authToken) {
    const newReq = req.clone({
      headers: req.headers.append('Authorization', 'Bearer ' + authToken),
    });
    console.log(authToken);
    return next(newReq).pipe(
      catchError((err: HttpErrorResponse) => {
        if (err.status === 401) {
          const refreshToken = authService.getRefreshToken();
          if (refreshToken) {
            return authService.refreshJwt(refreshToken).pipe(
              switchMap((response: RefreshTokenResponse) => {
                authService.setJwt(response.newJwtToken);
                const retryReq = req.clone({
                  setHeaders: {
                    Authorization: `Bearer ${response.newJwtToken}`,
                  },
                });
                return next(retryReq);
              }),
              catchError((refreshErr) => {
                authService.logOut();
                router.navigateByUrl('/user/login');
                return throwError(() => refreshErr);
              })
            );
          } else {
            authService.logOut();
            router.navigateByUrl('/user/login');
          }
        }
        return throwError(() => err);
      })
    );
  }

  return next(req);
};
