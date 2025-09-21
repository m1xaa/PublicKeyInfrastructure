export enum RevocationReason {
  Unspecified = 'UNSPECIFIED',
  KeyCompromise = 'KEY_COMPROMISE',
  CaCompromise = 'CA_COMPROMISE',
  AffiliationChanged = 'AFFILIATION_CHANGED',
  Superseded = 'SUPERSEDED',
  CessationOfOperation = 'CESSATION_OF_OPERATION',
  CertificateHold = 'CERTIFICATE_HOLD',
  RemoveFromCrl = 'REMOVE_FROM_CRL',
  PrivilegeWithdrawn = 'PRIVILEGE_WITHDRAWN',
  AaCompromise = 'AA_COMPROMISE'
}
