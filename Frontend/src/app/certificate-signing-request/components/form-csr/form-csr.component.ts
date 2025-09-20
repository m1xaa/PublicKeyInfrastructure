import { Component } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgIf, CommonModule, NgForOf } from '@angular/common';
import { AuthService } from '../../../infrastructure/service/auth.service';
import { CertificateServiceService } from '../../../generate-certificate/service/certificate-service.service';
import { CertificateResponse } from '../../../generate-certificate/model/certificate-response';
import { CertificateSigningRequestDTO } from '../../model/certificate-signing-request-dto';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-form-csr',
  standalone: true,
  imports: [
    ReactiveFormsModule, 
    NgIf, 
    CommonModule, 
    NgForOf
  ],
  templateUrl: './form-csr.component.html',
  styleUrl: './form-csr.component.css'
})
export class FormCsrComponent {
  form!: FormGroup;
  uploadedFileName: string | null = null;
  caCertificates: CertificateResponse[] = [];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private certificateService: CertificateServiceService,
    private router: Router,
    private toast: ToastrService
  ) {}

  ngOnInit() {
    this.instantiateForm();
    this.getPrerequisites();
  }
  
  instantiateForm() {
    this.form = this.fb.group({
      mode: ['autogenerate', Validators.required],
      caUser: ['', Validators.required],
      validTo: ['', Validators.required],
      commonName: ['', Validators.required],
      organization: [{value:'', disabled: true}, Validators.required],
      orgUnit: ['', Validators.required],
      country: ['', [Validators.required, Validators.maxLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      pemFile: [null],
    });
  
    this.form.get('mode')?.valueChanges.subscribe((mode) => {
      if (mode === 'autogenerate') {
        this.enableAutogenerateValidators();
      } else {
        this.enableSelfgenerateValidators();
      }
    });
  
    this.enableAutogenerateValidators();
  }

  getPrerequisites() {
    let userId = this.authService.getUser()?.userId;
    if (!userId)
      return;
    this.certificateService.getCACertificatesByUser(userId).subscribe({
      next: (response) => {
        this.form.get("organization")?.setValue(response.organizationName);
        this.caCertificates = response.certificates;
      }
    });
  }

  private enableAutogenerateValidators() {
    ['commonName', 'organization', 'orgUnit', 'country', 'email'].forEach((field) => {
      this.form.get(field)?.setValidators(Validators.required);
      this.form.get(field)?.updateValueAndValidity();
    });
    this.form.get('pemFile')?.clearValidators();
    this.form.get('pemFile')?.updateValueAndValidity();
  }

  private enableSelfgenerateValidators() {
    ['commonName', 'organization', 'orgUnit', 'country', 'email'].forEach((field) => {
      this.form.get(field)?.clearValidators();
      this.form.get(field)?.updateValueAndValidity();
    });
    this.form.get('pemFile')?.setValidators(Validators.required);
    this.form.get('pemFile')?.updateValueAndValidity();
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.uploadedFileName = file.name;
      this.form.patchValue({ pemFile: file });
      this.form.get('pemFile')?.updateValueAndValidity();
    }
  }

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    if (this.form.get("mode")?.value === "autogenerate") {
      const request: CertificateSigningRequestDTO = {
        caCertificateId: this.form.get("caUser")?.value,
        validTo: this.form.get("validTo")?.value,
        commonName: this.form.get("commonName")?.value,
        organizationalUnit: this.form.get("orgUnit")?.value,
        country: this.form.get("country")?.value,
        email: this.form.get("email")?.value
      };

      this.certificateService.createCSRAutogenerate(this.authService.getUser()!.userId, request).subscribe({
        next: () => {
          this.toast.success("Successfully created certificate");
          this.router.navigate(["/"]);
        }
      });
    }
    else {
      console.log("Not yet implemented");
    }
  }
}
