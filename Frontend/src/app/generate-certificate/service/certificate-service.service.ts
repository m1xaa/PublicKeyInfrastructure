import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {catchError, Observable} from 'rxjs';
import {CertificateResponse} from '../model/certificate-response';
import {environment} from '../../environment/environment';
import {handleHttpError} from '../../shared/error-handle/httpHandle';
import {CertificateRequestDTO} from '../model/certificate-request';
import {CertificateResponseDTO} from '../../certificates/model/CertificateResponseDTO';
import {CertificateDetailsDTO} from '../../certificates/model/CertificateDetailsDTO';
import { OrganizationCACertificatesResponseDTO } from '../../certificate-signing-request/model/organization-ca-certificates-response-dto';
import { CertificateSigningRequestDTO } from '../../certificate-signing-request/model/certificate-signing-request-dto';

@Injectable({
  providedIn: 'root'
})
export class CertificateServiceService {

  constructor(private http: HttpClient) { }

  getCertificatesParent(): Observable<CertificateResponse[]> {
    return this.http.get<CertificateResponse[]>(`${environment.apiHost}/api/certificates/parent`)
      .pipe(catchError(handleHttpError))
  }

  getCertificatesParentByOrganization(name?:String): Observable<CertificateResponse[]> {
    return this.http.get<CertificateResponse[]>(`${environment.apiHost}/api/certificates/parent/${name}`)
      .pipe(catchError(handleHttpError))
  }

  generateCertificate(req:CertificateRequestDTO): Observable<CertificateResponse> {
    return this.http.post<CertificateResponse>(`${environment.apiHost}/api/certificates`, req)
      .pipe(catchError(handleHttpError));
  }

  getAll():Observable<CertificateResponseDTO[]>{
    return this.http.get<CertificateResponseDTO[]>(`${environment.apiHost}/api/certificates`)
      .pipe(catchError(handleHttpError));
  }

  getCertificateDetails(id:string):Observable<CertificateDetailsDTO>{
    return this.http.get<CertificateDetailsDTO>(`${environment.apiHost}/api/certificates/${id}`)
      .pipe(catchError(handleHttpError));
  }

  getCACertificatesByUser(userId: number): Observable<OrganizationCACertificatesResponseDTO> {
    return this.http.get<OrganizationCACertificatesResponseDTO>(`${environment.apiHost}/api/certificates/by-user/${userId}`)
      .pipe(catchError(handleHttpError));
  }

  createCSRAutogenerate(userId: number, request: CertificateSigningRequestDTO): Observable<void> {
    return this.http.post<void>(`${environment.apiHost}/api/certificates/csr-autogenerate/for-user/${userId}`, request)
      .pipe(catchError(handleHttpError));
  }

  createCSRSelfgenerate(userId: number, request: FormData): Observable<void> {
    return this.http.post<void>(`${environment.apiHost}/api/certificates/csr-selfgenerate/for-user/${userId}`, request)
      .pipe(catchError(handleHttpError));
  }

  downloadKeyStore(certificateId: string) {
    console.log(certificateId);
    // MUST BE 'http://localhost:8080/', DO NOT USE .env
    // Switched to env and works now
    return this.http.get<Blob>(`${environment.apiHost}/api/certificates/${certificateId}/download`, {
      responseType: 'blob' as 'json',
    });
  }
}
