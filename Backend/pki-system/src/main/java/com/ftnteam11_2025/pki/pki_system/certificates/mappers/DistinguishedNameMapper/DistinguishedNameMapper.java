package com.ftnteam11_2025.pki.pki_system.certificates.mappers.DistinguishedNameMapper;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateRequestDTO;
import org.bouncycastle.asn1.x500.X500Name;

public class DistinguishedNameMapper {
    public static X500Name toX500Name(CertificateRequestDTO dto) {
        // Grade DN string
        String dn = String.format(
                "CN=%s %s, GIVENNAME=%s, O=%s, OU=%s, C=%s, EMAILADDRESS=%s",
                dto.getCommonName(),
                dto.getSurname() != null ? dto.getSurname() : "",
                dto.getGivenName() != null ? dto.getGivenName() : "",
                dto.getOrganization() != null ? dto.getOrganization() : "",
                dto.getOrganizationalUnit() != null ? dto.getOrganizationalUnit() : "",
                dto.getCountry() != null ? dto.getCountry() : "",
                dto.getEmail() != null ? dto.getEmail() : ""
        );

        return new X500Name(dn);
    }

    public static String toDistinguishedNameString(CertificateRequestDTO dto) {
        return String.format(
                "CN=%s %s, GIVENNAME=%s, O=%s, OU=%s, C=%s, EMAILADDRESS=%s",
                dto.getCommonName(),
                dto.getSurname() != null ? dto.getSurname() : "",
                dto.getGivenName() != null ? dto.getGivenName() : "",
                dto.getOrganization() != null ? dto.getOrganization() : "",
                dto.getOrganizationalUnit() != null ? dto.getOrganizationalUnit() : "",
                dto.getCountry() != null ? dto.getCountry() : "",
                dto.getEmail() != null ? dto.getEmail() : ""
        );
    }
}
