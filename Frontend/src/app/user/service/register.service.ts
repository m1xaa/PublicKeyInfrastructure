import { Injectable } from '@angular/core';
import { RegisterRequest } from '../model/register.request';
import { catchError, Observable, throwError } from 'rxjs';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../environment/environment';
import { ErrorResponse } from '../../shared/model/error.response.model';
import { RegisterResponse } from '../model/register.response.model';

@Injectable({
  providedIn: 'root',
})
export class RegisterService {
  constructor(private httpClient: HttpClient) {}

  register(registerRequest: RegisterRequest): Observable<RegisterResponse> {
    const formData = new FormData();
    formData.append('firstName', registerRequest.firstName);
    formData.append('lastName', registerRequest.lastName);
    formData.append('organizationName', registerRequest.organizationName);
    formData.append('email', registerRequest.email);
    formData.append('password', registerRequest.password);
    formData.append('userRole', registerRequest.userRole);

    return this.httpClient
      .post<RegisterResponse>(
        environment.apiHost + '/api/user/register',
        formData
      )
      .pipe(catchError(this.handleError));
  }

  activateAccount(activationCode: string): Observable<void> {
    return this.httpClient
      .post<void>(environment.apiHost + '/api/user/activate', {
        verificationCode: activationCode,
      })
      .pipe(catchError(this.handleError));
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
