import {HttpErrorResponse} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {ErrorResponse} from '../model/error.response.model';

export function handleHttpError(error: HttpErrorResponse): Observable<never> {
  let errorResponse: ErrorResponse | null = null;

  if (error.error && typeof error.error === 'object') {
    errorResponse = error.error as ErrorResponse;
  }

  return throwError(() => ({
    code: error.status,
    message: errorResponse?.message ?? error.message,
    errors: errorResponse?.errors,
  } as ErrorResponse));
}
