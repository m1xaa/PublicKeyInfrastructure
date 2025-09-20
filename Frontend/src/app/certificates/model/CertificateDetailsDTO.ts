import {SubjectIssuerDTO} from './SubjectIssuerDTO';

export interface CertificateDetailsDTO {
  id: string;

  issuer: SubjectIssuerDTO;

  subject: SubjectIssuerDTO;
  validFrom: Date;
  validTo: Date;

  certificateKey: string;
  publicKey: string;
}
