import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {catchError, Observable} from 'rxjs';
import {CertificateResponse} from '../model/certificate-response';
import {environment} from '../../environment/environment';
import {handleHttpError} from '../../shared/error-handle/httpHandle';
import {CertificateRequestDTO} from '../model/certificate-request';

@Injectable({
  providedIn: 'root'
})
export class CertificateServiceService {

  constructor(private http: HttpClient) { }

  getCertificatesParent(): Observable<CertificateResponse[]> {
    return this.http.get<CertificateResponse[]>(`${environment.apiHost}/api/certificate/parent`)
      .pipe(catchError(handleHttpError))
  }

  generateCertificate(req:CertificateRequestDTO): Observable<CertificateResponse> {
    return this.http.post<CertificateResponse>(`${environment.apiHost}/api/certificate`, req)
      .pipe(catchError(handleHttpError));
  }
}
