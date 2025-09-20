export interface Extensions {
  pathLen?: number, // root, ca
  subjectKeyIdentifier?:boolean, // root
  authorityKeyIdentifier?:boolean, // root
  serverAuth?:boolean,
  clientAuth?:boolean
}
