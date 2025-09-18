import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environment/environment';

@Injectable({
  providedIn: 'root',
})
export class CaService {
  constructor(private httpClient: HttpClient) {}

  getPendingUsers(page: number, size: number = 8): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.httpClient.get(`${environment.apiHost}/api/users/all`, {
      params,
    });
  }

  acceptUser(email: string): Observable<Boolean> {
    const formData = new FormData();
    formData.append('email', email);
    return this.httpClient.post<boolean>(
      `${environment.apiHost}/api/users/account/accept`,
      formData
    );
  }

  rejectUser(email: string): Observable<boolean> {
    const formData = new FormData();
    formData.append('email', email);
    return this.httpClient.post<boolean>(
      `${environment.apiHost}/api/users/account/reject`,
      formData
    );
  }
}
