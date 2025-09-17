package com.ftnteam11_2025.pki.pki_system.certificates.mappers.DistinguishedNameMapper;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateRequestDTO;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;

public class DistinguishedNameMapper {
    public static X500Name buildX500Name(
            String commonName,
            String surname,
            String givenName,
            String organization,
            String organizationalUnit,
            String country,
            String email) {

        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);

        builder.addRDN(BCStyle.CN, commonName);

        if (surname != null && !surname.isBlank()) {
            builder.addRDN(BCStyle.SURNAME, surname);
        }
        if (givenName != null && !givenName.isBlank()) {
            builder.addRDN(BCStyle.GIVENNAME, givenName);
        }
        if (organization != null && !organization.isBlank()) {
            builder.addRDN(BCStyle.O, organization);
        }
        if (organizationalUnit != null && !organizationalUnit.isBlank()) {
            builder.addRDN(BCStyle.OU, organizationalUnit);
        }
        if (country != null && !country.isBlank()) {
            builder.addRDN(BCStyle.C, country);
        }
        if (email != null && !email.isBlank()) {
            builder.addRDN(BCStyle.EmailAddress, email);
        }

        return builder.build();
    }

    private static String getValue(X500Name name, ASN1ObjectIdentifier oid) {
        RDN[] rdns = name.getRDNs(oid);
        if (rdns != null && rdns.length > 0) {
            ASN1Encodable value = rdns[0].getFirst().getValue();
            return value.toString();
        }
        return "";
    }

    public static String toDistinguishedNameString(X500Name name) {
        return String.format(
                "CN=%s, SURNAME=%s, GIVENNAME=%s, O=%s, OU=%s, C=%s, EMAILADDRESS=%s",
                getValue(name, BCStyle.CN),
                getValue(name, BCStyle.SURNAME),
                getValue(name, BCStyle.GIVENNAME),
                getValue(name, BCStyle.O),
                getValue(name, BCStyle.OU),
                getValue(name, BCStyle.C),
                getValue(name, BCStyle.EmailAddress)
        );
    }
}
