export interface CertificateSigningRequestDTO {
    caCertificateId: number;
    validTo: Date;
    commonName: string;
    organizationalUnit: string;
    country: string;
    email: string;
}