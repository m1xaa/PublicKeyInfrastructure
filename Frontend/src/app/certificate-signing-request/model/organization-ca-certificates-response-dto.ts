import { CertificateResponseDTO } from "../../certificates/model/CertificateResponseDTO";
import { CertificateResponse } from "../../generate-certificate/model/certificate-response";

export interface OrganizationCACertificatesResponseDTO {
    organizationName: string;
    certificates: CertificateResponse[];
}