
CREATE TABLE certificate_revocation_lists (
      id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      revocation_reason   VARCHAR(50) NOT NULL,
      certificate_authority_id UUID   NOT NULL,
      generated_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,

      CONSTRAINT fk_crl_certificate_authority FOREIGN KEY (certificate_authority_id)
          REFERENCES certificates (id)
          ON DELETE CASCADE,

      CONSTRAINT uc_crl_certificate_authority UNIQUE (certificate_authority_id)
);
