import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {catchError, Observable} from 'rxjs';
import {CertificateResponse} from '../model/certificate-response';
import {environment} from '../../environment/environment';
import {handleHttpError} from '../../shared/error-handle/httpHandle';
import {CertificateRequestDTO} from '../model/certificate-request';
import {CertificateResponseDTO} from '../../certificates/model/CertificateResponseDTO';
import {CertificateDetailsDTO} from '../../certificates/model/CertificateDetailsDTO';

@Injectable({
  providedIn: 'root'
})
export class CertificateServiceService {

  constructor(private http: HttpClient) { }

  getCertificatesParent(): Observable<CertificateResponse[]> {
    return this.http.get<CertificateResponse[]>(`${environment.apiHost}/api/certificates/parent`)
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

  downloadKeyStore(certificateId: string) {
    console.log(certificateId);
    // MUST BE 'http://localhost:8080/', DO NOT USE .env
    return this.http.get<Blob>(`http://localhost:8080/api/certificates/${certificateId}/download`, {
      responseType: 'blob' as 'json',
    });
  }
}
