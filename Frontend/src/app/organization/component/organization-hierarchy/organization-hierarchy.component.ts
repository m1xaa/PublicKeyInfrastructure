import { Component, Input } from '@angular/core';
import { NgForOf, NgTemplateOutlet, NgIf } from '@angular/common';
import { OrganizationHierarchy } from '../../model/organization-hierarchy';

@Component({
  selector: 'app-organization-hierarchy',
  standalone: true,
  imports: [NgTemplateOutlet, NgForOf],
  templateUrl: './organization-hierarchy.component.html',
  styleUrl: './organization-hierarchy.component.css',
})
export class OrganizationHierarchyComponent {
  @Input() hierarchy!: OrganizationHierarchy[];
  formatDate(date: string): string {
    return new Date(date).toLocaleDateString();
  }
}
