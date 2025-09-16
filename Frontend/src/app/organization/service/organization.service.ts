import { Injectable } from '@angular/core';
import {OrganizationResponseDTO} from '../model/organization-responseDTO';
import {catchError, Observable} from 'rxjs';
import {environment} from '../../environment/environment';
import {HttpClient} from '@angular/common/http';
import {handleHttpError} from '../../shared/error-handle/httpHandle';

@Injectable({
  providedIn: 'root'
})
export class OrganizationService {

  constructor(private http: HttpClient) { }

  getAllOrganizations(): Observable<OrganizationResponseDTO[]> {
    return this.http.get<OrganizationResponseDTO[]>(`${environment.apiHost}/api/organizations`)
      .pipe(catchError(handleHttpError))
  }
}
