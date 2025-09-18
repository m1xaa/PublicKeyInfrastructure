import { Component, OnInit } from '@angular/core';
import { RegisterResponse } from '../model/register.response.model';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { OrganizationResponseDTO } from '../../organization/model/organization-responseDTO';
import { ToastrService } from 'ngx-toastr';
import { UserRole } from '../../infrastructure/auth/model/user-role.model';
import { matchPasswordsValidator } from '../../infrastructure/validators/matchPasswordsValidator';
import { passwordValidator } from '../../infrastructure/validators/passwordValidator';
import { OrganizationService } from '../../organization/service/organization.service';
import { ErrorResponse } from '../../shared/model/error.response.model';
import { RegisterRequest } from '../model/register.request';
import { RegisterService } from '../service/register.service';
import { RegisterCARequest } from '../model/register.request.ca.model';

@Component({
  selector: 'app-register-ca',
  standalone: false,
  templateUrl: './register-ca.component.html',
  styleUrl: './register-ca.component.css',
})
export class RegisterCaComponent implements OnInit {
  form!: FormGroup;
  showPassword = false;
  showConfirmPassword = false;
  waitingResponse = false;
  organizations: OrganizationResponseDTO[] = [];

  showDialog: boolean = false;
  dialogData: {
    registerResponse: RegisterResponse;
  } | null = null;

  constructor(
    private fb: FormBuilder,
    private registerService: RegisterService,
    private toastService: ToastrService,
    private organizationService: OrganizationService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      organizationId: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
    });

    this.organizationService.getAllOrganizations().subscribe({
      next: (response: OrganizationResponseDTO[]) => {
        this.organizations = response;
      },
    });
  }

  get firstName() {
    return this.form.get('firstName');
  }

  get lastName() {
    return this.form.get('lastName');
  }

  get organizationId() {
    return this.form.get('organizationId');
  }
  get email() {
    return this.form.get('email');
  }

  async onSubmit() {
    if (this.form.valid) {
      this.waitingResponse = true;
      const formValues = this.form.value;
      const registerRequest: RegisterCARequest = {
        firstName: formValues.firstName,
        lastName: formValues.lastName,
        organizationId: formValues.organizationId,
        email: formValues.email,
        userRole: UserRole.Ca,
      };
      this.registerService.registerCA(registerRequest).subscribe({
        next: (response) => {
          this.waitingResponse = false;
          this.form.reset();
          this.toastService.success('You successfully created CA account');
        },
        error: (error: ErrorResponse) => {
          console.log(error);
          this.waitingResponse = false;
          this.toastService.error(error.message, 'Failed to register');

          if (error.errors) {
            Object.keys(error.errors).forEach((fieldName) => {
              const control = this.form.get(fieldName);
              if (control) {
                control.setErrors({ serverError: error.errors[fieldName] });
              }
            });
          }
        },
      });
    }
  }
}
