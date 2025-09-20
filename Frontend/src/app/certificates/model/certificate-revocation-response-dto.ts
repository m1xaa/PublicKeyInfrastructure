import { RevocationReason } from "./revocation-reason";

export interface CertificateRevocationResponseDTO {
    id: string;
    certificateId: string;
    commonName: string;
    generatedAt: string;
    revocationReason: RevocationReason;
}