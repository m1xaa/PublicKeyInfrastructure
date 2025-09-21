import { RevocationReason } from "./revocation-reason";

export interface RevokeCertificateDTO {
    reason: RevocationReason;
}