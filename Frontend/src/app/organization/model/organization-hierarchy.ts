import {OrganizationNode} from './organization-node';

export interface OrganizationHierarchy {
  organizationId: number;
  organizationName: string;
  rootCertificates: OrganizationNode[];
}
