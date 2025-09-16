import { Component } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';
import {CertificateRequestDTO} from '../../model/certificate-request';
import {futureDateRangeValidator} from '../../../shared/functions/ValidateStartEndDate';
import {UserResponse} from '../../../user/model/UserResponse';
import {UserServiceService} from '../../../user/service/user-service.service';
import {ToastrService} from 'ngx-toastr';
import {CertificateResponse} from '../../model/certificate-response';
import {CertificateServiceService} from '../../service/certificate-service.service';

@Component({
  selector: 'app-form-certificate',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgForOf,
    NgIf
  ],
  templateUrl: './form-certificate.component.html',
  styleUrl: './form-certificate.component.css'
})
export class FormCertificateComponent {
  users: UserResponse[] = [];
  existingCerts:CertificateResponse[] = []

  form!: FormGroup;
  today!: string;

  constructor(private fb: FormBuilder, private userService: UserServiceService,
              private toast:ToastrService, private certService:CertificateServiceService) {}



  ngOnInit() {
    const now = new Date()
    this.today = now.toISOString().split('T')[0];
    this.form = this.fb.group({
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
      certificateId: ['']
    }, {
      validators: futureDateRangeValidator()
    });
    this.form.get('certificateType')!.valueChanges.subscribe(type => {
      if (type === 'Root') {
        this.form.get('parentCert')!.reset();
      }
    });
    this.form.get('certificateType')!.valueChanges.subscribe(type => {
      const certIdControl = this.form.get('certificateId')!;
      if (type === 'CA' || type === 'EndEntity') {
        certIdControl.setValidators([Validators.required]);
      } else {
        certIdControl.clearValidators();
        certIdControl.reset();
      }
      certIdControl.updateValueAndValidity();
    });
    this.getAllUsers();
    this.getParentCertificates();
  }

  isInvalid(controlName: string): boolean {
    const control = this.form.get(controlName);
    return !!control && control.invalid && (control.touched || control.dirty);
  }

  getAllUsers() {
    this.userService.getAllUsers().subscribe({
      next: (users: UserResponse[]) => {
        this.users = users;
      },
      error: (err) => {
        this.toast.error(err.message, 'Error');
      }
    });
  }

  getParentCertificates(){
    this.certService.getCertificatesParent().subscribe({
      next: (certs:CertificateResponse[]) => {
        this.existingCerts = certs;
      },
      error: (err) => {
        this.toast.error(err.message, 'Error');
      }
    })
  }

  get showParentDropdown() {
    const t = this.form.get('certificateType')!.value;
    return t === 'CA' || t === 'EndEntity';
  }

  submit() {
    console.log((this.form.value))
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      console.log('Form is invalid');
      return;
    }
    const dto: CertificateRequestDTO = this.form.value;
    this.certService.generateCertificate(dto).subscribe({
      next: (cert: CertificateResponse) => {
        console.log(cert);
        this.toast.success('Certificate generated successfully!', 'Success');
        this.form.reset();
      },
      error: (err) => {
        this.toast.error(err.message, 'Error');
      }
    })

  }
}
