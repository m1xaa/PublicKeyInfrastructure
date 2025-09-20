import {Component} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators,} from '@angular/forms';
import {AsyncPipe, NgForOf, NgIf} from '@angular/common';
import {CertificateRequestDTO} from '../../model/certificate-request';
import {futureDateRangeValidator} from '../../../shared/functions/ValidateStartEndDate';
import {UserResponse} from '../../../user/model/UserResponse';
import {UserServiceService} from '../../../user/service/user-service.service';
import {ToastrService} from 'ngx-toastr';
import {CertificateResponse} from '../../model/certificate-response';
import {CertificateServiceService} from '../../service/certificate-service.service';
import {OrganizationService} from '../../../organization/service/organization.service';
import {OrganizationResponseDTO} from '../../../organization/model/organization-responseDTO';
import {OrganizationHierarchy} from '../../../organization/model/organization-hierarchy';
import {
  OrganizationHierarchyComponent
} from '../../../organization/component/organization-hierarchy/organization-hierarchy.component';
import {MatAutocomplete, MatAutocompleteTrigger, MatOption} from '@angular/material/autocomplete';
import {AuthService} from '../../../infrastructure/service/auth.service';
import {UserRole} from '../../../infrastructure/auth/model/user-role.model';
import {map, Observable} from 'rxjs';

@Component({
  selector: 'app-form-certificate',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgForOf,
    NgIf,
    OrganizationHierarchyComponent,
    MatAutocomplete,
    MatOption,
    MatAutocompleteTrigger,
    AsyncPipe,

  ],
  templateUrl: './form-certificate.component.html',
  styleUrl: './form-certificate.component.css',
})
export class FormCertificateComponent {
  users: UserResponse[] = [];
  existingCerts: CertificateResponse[] = [];
  organizations: OrganizationResponseDTO[] = [];
  hierarchyOrgs: OrganizationHierarchy[] = [];
  organizationName?:String = ""
  form!: FormGroup;
  extensionForm!: FormGroup;
  today!: string;



  newOrganization = new FormControl('');

  constructor(
    private fb: FormBuilder,
    private userService: UserServiceService,
    private toast: ToastrService,
    private certService: CertificateServiceService,
    private organizationService: OrganizationService,
    private authService:AuthService
  ) {}

  ngOnInit() {
    const now = new Date();
    this.today = now.toISOString().split('T')[0];
    this.extensionForm = this.fb.group({
      pathLen: [null],
      subjectKeyIdentifier: [false],
      authorityKeyIdentifier: [false],
      serverAuth: [false],
      clientAuth: [false]
    })
    this.form = this.fb.group(
      {
        commonName: ['', Validators.required],
        surname: ['', Validators.required],
        givenName: ['', Validators.required],
        organization: ['', Validators.required],
        organizationalUnit: ['', Validators.required],
        country: ['', Validators.required],
        email: ['', [Validators.required, Validators.email]],
        userId: [null, Validators.required],
        validFrom: ['', Validators.required],
        validTo: ['', Validators.required],
        certificateType: ['', Validators.required],
        certificateId: [''],

        extensions: this.extensionForm,
      },
      { validators: futureDateRangeValidator() }
    );

    // Reset extension polja pri promeni tipa sertifikata
    const extensionsGroup = this.form.get('extensions') as FormGroup;
    this.form.get('certificateType')!.valueChanges.subscribe(type => {
      if(type === 'RootCA' || type === 'CA') {
        extensionsGroup.patchValue({ serverAuth: false, clientAuth: false });
      } else if(type === 'EndEntity') {
        extensionsGroup.patchValue({ pathLen: null, subjectKeyIdentifier: false, authorityKeyIdentifier: false });
      }
    });

    // Validacija parent certificate za CA i EndEntity
    this.form.get('certificateType')!.valueChanges.subscribe((type) => {
      const certIdControl = this.form.get('certificateId')!;
      if (type === 'CA' || type === 'EndEntity') {
        certIdControl.setValidators([Validators.required]);
        this.getParentCertificates();
      } else {
        certIdControl.clearValidators();
        certIdControl.reset();
      }
      certIdControl.updateValueAndValidity();
    });

    this.getOrganizationHierarchy();
    this.getAllOrganizations();

    if (this.isCA$) {
      this.form.get('organization')?.setValue(this.organizationName);
      this.form.get('organization')?.disable();
    }
  }


  isInvalid(controlName: string): boolean {
    const control = this.form.get(controlName);
    return !!control && control.invalid && (control.touched || control.dirty);
  }

  getAllUsers(name: string = '') {
    if(this.isCA$){
      this.userService.getAllUsersByOrganization(name).subscribe({
        next: (users: UserResponse[]) => {
          this.users = users;
        },
        error: (err) => {
          this.toast.error(err.message, 'Error');
        },
      })
    }else{
      this.userService.getAllUsers().subscribe({
        next: (users: UserResponse[]) => {

          this.users = users;
        },
        error: (err) => {
          this.toast.error(err.message, 'Error');
        },
      });
    }
  }

  getParentCertificates() {
    if(this.isCA$){
      this.certService.getCertificatesParentByOrganization(this.organizationName).subscribe({
        next: (certs: CertificateResponse[]) => {
          this.existingCerts = certs;
        },
        error: (err) => {
          this.toast.error(err.message, 'Error');
        },
      })
    }else{
      this.certService.getCertificatesParent().subscribe({
        next: (certs: CertificateResponse[]) => {
          this.existingCerts = certs;
        },
        error: (err) => {
          this.toast.error(err.message, 'Error');
        },
      });
    }

  }

  getAllOrganizations() {
    this.organizationService.getAllOrganizations().subscribe({
      next: (res: OrganizationResponseDTO[]) => {
        this.organizations = res;
      },
      error: (err) => {
        this.toast.error(err.message, 'Error');
      },
    });
  }

  getOrganizationHierarchy() {
    if(this.isCA$){
      this.organizationService.getHierarchyByOrganization().subscribe({
        next: (res: OrganizationHierarchy) => {
          this.hierarchyOrgs = [res];
          this.organizationName = res.organizationName;
          this.form.get('organization')?.setValue(this.organizationName);
          this.getAllUsers(res.organizationName);
          this.getParentCertificates();
        },
        error: (err) => {
          this.toast.error(err.message, 'Error');
        },
      })
    }else{
      this.organizationService.getHierarchy().subscribe({
        next: (res: OrganizationHierarchy[]) => {
          this.hierarchyOrgs = res;
          console.log(res);
        },
        error: (err) => {
          this.toast.error(err.message, 'Error');
        },
      });
    }

  }

  get isCA$(): Observable<boolean> {
    return this.authService.userRole$.pipe(map((role) => role === UserRole.Ca));
  }

  submit() {

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      console.log('Form is invalid');
      return;
    }
    const dto: CertificateRequestDTO = this.form.getRawValue();
    console.log(dto);

    this.certService.generateCertificate(dto).subscribe({
      next: (cert: CertificateResponse) => {
        console.log(cert);
        this.toast.success('Certificate generated successfully!', 'Success');
        this.form.reset();
        this.getAllOrganizations();
        this.getOrganizationHierarchy();
      },
      error: (err) => {
        this.toast.error(err.message, 'Error');
      },
    });
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
        this.getOrganizationHierarchy();
        this.toast.success('You add new organizations', 'Success');
      },
      error: (err) => {
        this.toast.error(err.message, 'Error');
      },
    });
  }
}
