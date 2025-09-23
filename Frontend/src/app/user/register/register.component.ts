import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RegisterService } from '../service/register.service';
import { passwordValidator } from '../../infrastructure/validators/passwordValidator';
import { matchPasswordsValidator } from '../../infrastructure/validators/matchPasswordsValidator';
import { RegisterRequest } from '../model/register.request';
import { UserRole } from '../../infrastructure/auth/model/user-role.model';
import { RegisterResponse } from '../model/register.response.model';
import { ErrorResponse } from '../../shared/model/error.response.model';
import { ToastrService } from 'ngx-toastr';
import { OrganizationResponseDTO } from '../../organization/model/organization-responseDTO';
import { OrganizationService } from '../../organization/service/organization.service';
import zxcvbn from 'zxcvbn';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-register',
  standalone: false,
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
})
export class RegisterComponent implements OnInit {
  form!: FormGroup;
  showPassword = false;
  showConfirmPassword = false;
  waitingResponse = false;
  organizations: OrganizationResponseDTO[] = [];

  showDialog: boolean = false;
  dialogData: {
    registerResponse: RegisterResponse;
  } | null = null;

  passwordStrengthScore: number = 0;

  constructor(
    private fb: FormBuilder,
    private registerService: RegisterService,
    private toastService: ToastrService,
    private organizationService: OrganizationService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group(
      {
        firstName: ['', Validators.required],
        lastName: ['', Validators.required],
        organizationId: ['', Validators.required],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, passwordValidator()]],
        confirmPassword: ['', Validators.required],
      },
      { validators: matchPasswordsValidator('password', 'confirmPassword') }
    );

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

  get password() {
    return this.form.get('password');
  }

  get confirmPassword() {
    return this.form.get('confirmPassword');
  }

  togglePasswordVisibility(field: 'password' | 'confirmPassword') {
    if (field === 'password') {
      this.showPassword = !this.showPassword;
    } else {
      this.showConfirmPassword = !this.showConfirmPassword;
    }
  }

  openRegisterDialog(response: RegisterResponse) {
    this.dialogData = { registerResponse: response };
    this.showDialog = true;
  }

  async onSubmit() {
    if (this.form.valid) {
      this.waitingResponse = true;
      const formValues = this.form.value;
      const registerRequest: RegisterRequest = {
        firstName: formValues.firstName,
        lastName: formValues.lastName,
        organizationId: formValues.organizationId,
        email: formValues.email,
        password: formValues.password,
        userRole: UserRole.Regular,
      };
      console.log(registerRequest);
      this.registerService
        .register(registerRequest)
        .pipe(finalize(() => (this.waitingResponse = false)))
        .subscribe({
          next: (response: RegisterResponse) => {
            this.form.reset();
            this.openRegisterDialog(response);
          },
          error: (error: ErrorResponse) => {
            this.toastService.error(error.message, 'Failed to register');
          },
        });
    }
  }

  evaluatePasswordStrength(password: string) {
    if (!this.password?.valid) {
      this.passwordStrengthScore = 0;
    } else {
      const result = zxcvbn(password);
      this.passwordStrengthScore = result.score;
    }
  }

  getStrengthColor(score: number): string {
    switch (score) {
      case 0:
        return 'red';
      case 1:
        return 'orange';
      case 2:
        return 'yellow';
      case 3:
        return 'lightgreen';
      case 4:
        return 'green';
      default:
        return 'transparent';
    }
  }
}
