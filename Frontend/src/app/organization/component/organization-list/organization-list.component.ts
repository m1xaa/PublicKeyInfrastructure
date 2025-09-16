import {Component, Input} from '@angular/core';
import {OrganizationResponseDTO} from '../../model/organization-responseDTO';
import {NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-organization-list',
  standalone: true,
  imports: [
    NgIf,
    NgForOf
  ],
  templateUrl: './organization-list.component.html',
  styleUrl: './organization-list.component.css'
})
export class OrganizationListComponent {
  @Input() organizations: OrganizationResponseDTO[] = [];

  isOpen = false;

  toggle() {
    this.isOpen = !this.isOpen;
  }
}
