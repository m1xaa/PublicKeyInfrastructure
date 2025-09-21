import {CertificateStatus} from './CertificateStatus';

export interface CertificateResponseDTO {
  id: string;
  issuerId: string;
  commonName: string;
  email: string;
  validFrom: Date;
  validTo: Date;
  status: CertificateStatus
}
