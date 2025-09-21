import { Component, OnInit } from '@angular/core';
import { OrganizationListComponent } from '../organization-list/organization-list.component';
import { FormBuilder, FormControl, ReactiveFormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { OrganizationResponseDTO } from '../../model/organization-responseDTO';
import { OrganizationService } from '../../service/organization.service';

@Component({
  selector: 'app-organizations-page',
  standalone: true,
  imports: [OrganizationListComponent, ReactiveFormsModule],
  templateUrl: './organizations-page.component.html',
  styleUrl: './organizations-page.component.css',
})
export class OrganizationsPageComponent implements OnInit {
  newOrganization = new FormControl('');
  organizations: OrganizationResponseDTO[] = [];

  constructor(
    private fb: FormBuilder,
    private toast: ToastrService,
    private organizationService: OrganizationService
  ) {}

  getAllOrganizations() {
    this.organizationService.getAllOrganizations().subscribe({
      next: (res: OrganizationResponseDTO[]) => {
        this.organizations = res;
      },
    });
  }

  ngOnInit(): void {
    this.getAllOrganizations();
  }

  addOrganization() {
    const name = this.newOrganization.value?.trim();
    if (!name) {
      this.toast.warning('Organization name cannot be empty', 'Warning');
      return;
    }
    this.organizationService.createOrganization(name).subscribe({
      next: (response) => {
        this.getAllOrganizations();
        this.toast.success('You add new organizations', 'Success');
      },
      error: (err) => {
        this.toast.error(err.message, 'Error');
      },
    });
  }
}
