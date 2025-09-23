export interface OrganizationNode {
  id: string;
  commonName: string;
  type: string;
  status: string;
  validFrom: string;
  validTo: string;
  children: OrganizationNode[];
}
