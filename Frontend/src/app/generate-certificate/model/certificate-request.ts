import {ExtensionDTO} from './cert-exntension';
import {CertificateType} from './cert-type';
import {Extensions} from './extensions';

export interface CertificateRequestDTO {
  commonName: string;
  surname: string;
  givenName: string;
  organization: string;
  organizationalUnit: string;
  country: string;
  email: string;

  userId: number;

  validFrom: Date;
  validTo: Date;

  certificateId?: string;   // UUID može biti opciono
  certificateType: CertificateType;
  extensions?: Extensions;
}
