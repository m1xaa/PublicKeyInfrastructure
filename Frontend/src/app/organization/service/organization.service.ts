import { Injectable } from '@angular/core';
import { OrganizationResponseDTO } from '../model/organization-responseDTO';
import { catchError, Observable } from 'rxjs';
import { environment } from '../../environment/environment';
import { HttpClient } from '@angular/common/http';
import { handleHttpError } from '../../shared/error-handle/httpHandle';
import { OrganizationHierarchy } from '../model/organization-hierarchy';
import { Organization } from '../model/organization.model';

@Injectable({
  providedIn: 'root',
})
export class OrganizationService {
  constructor(private http: HttpClient) {}

  getAllOrganizations(): Observable<OrganizationResponseDTO[]> {
    return this.http
      .get<OrganizationResponseDTO[]>(
        `${environment.apiHost}/api/organizations`
      )
      .pipe(catchError(handleHttpError));
  }

  getHierarchy(): Observable<OrganizationHierarchy[]> {
    return this.http
      .get<OrganizationHierarchy[]>(
        `${environment.apiHost}/api/organizations/hierarchy`
      )
      .pipe(catchError(handleHttpError));
  }

  createOrganization(name: string): Observable<OrganizationResponseDTO> {
    const formData = new FormData();
    formData.append('name', name);

    return this.http
      .post<OrganizationResponseDTO>(
        `${environment.apiHost}/api/organizations/add`,
        formData
      )
      .pipe(catchError(handleHttpError));
  }
}
