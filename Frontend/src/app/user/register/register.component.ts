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

  showDialog: boolean = false;
  dialogData: {
    registerResponse: RegisterResponse;
  } | null = null;

  constructor(
    private fb: FormBuilder,
    private registerService: RegisterService,
    private toastService: ToastrService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group(
      {
        firstName: ['', Validators.required],
        lastName: ['', Validators.required],
        organizationName: ['', Validators.required],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, passwordValidator()]],
        confirmPassword: ['', Validators.required],
      },
      { validators: matchPasswordsValidator('password', 'confirmPassword') }
    );
  }

  get firstName() {
    return this.form.get('firstName');
  }

  get lastName() {
    return this.form.get('lastName');
  }

  get organizationName() {
    return this.form.get('organizationName');
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
        organizationName: formValues.organizationName,
        email: formValues.email,
        password: formValues.password,
        userRole: UserRole.Regular,
      };
      this.registerService.register(registerRequest).subscribe({
        next: (response: RegisterResponse) => {
          this.waitingResponse = false;
          this.form.reset();
          this.openRegisterDialog(response);
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
