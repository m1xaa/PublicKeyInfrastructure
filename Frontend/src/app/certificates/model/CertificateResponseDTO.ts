import {CertificateStatus} from './CertificateStatus';

export interface CertificateResponseDTO {
  id: string;
  commonName: string;
  email: string;
  validFrom: Date;
  validTo: Date;
  status: CertificateStatus
}
