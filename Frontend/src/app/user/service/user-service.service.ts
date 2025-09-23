import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {catchError, Observable} from 'rxjs';
import {UserResponse} from '../model/UserResponse';
import {environment} from '../../environment/environment';
import {handleHttpError} from '../../shared/error-handle/httpHandle';

@Injectable({
  providedIn: 'root'
})
export class UserServiceService {

  constructor(private http: HttpClient) { }

  getAllUsers(): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(`${environment.apiHost}/api/user`)
      .pipe(catchError(handleHttpError))
  }

  getAllUsersByOrganization(name?:String): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(`${environment.apiHost}/api/user/${name}`)
      .pipe(catchError(handleHttpError))
  }
}
