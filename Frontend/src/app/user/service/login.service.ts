import { Injectable } from '@angular/core';
import { AuthService } from '../../infrastructure/service/auth.service';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { catchError, map, Observable, tap, throwError } from 'rxjs';
import { environment } from '../../environment/environment';
import { User } from '../../infrastructure/auth/model/user.mode';
import { ErrorResponse } from '../../shared/model/error.response.model';
import { LoginResponse } from '../model/login.response.model';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  constructor(
    private authService: AuthService,
    private httpClient: HttpClient
  ) {}

  login(email: string, password: string): Observable<LoginResponse> {
    return this.httpClient
      .post<LoginResponse>(environment.apiHost + '/api/user/login', {
        email,
        password,
      })
      .pipe(
        tap((response: LoginResponse) => {
          const user: User = {
            userId: response.userId,
            email: response.email,
            firstName: response.firstName,
            lastName: response.lastName,
            role: response.role,
          };
          this.authService.setJwt(response.jwt);
          this.authService.setUser(user);
          this.authService.setRefreshToken(response.refreshToken);
          console.log(response.jwt);
          console.log(response.refreshToken);
        }),
        catchError(this.handleError)
      );

  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorResponse: ErrorResponse | null = null;

    if (error.error && typeof error.error === 'object') {
      errorResponse = error.error as ErrorResponse;
    }

    return throwError(
      () =>
        ({
          code: error.status,
          message: errorResponse?.message ?? error.message,
          errors: errorResponse?.errors,
        } as ErrorResponse)
    );
  }
}
