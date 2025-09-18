import { Component, OnInit } from '@angular/core';
import { VerificationStatus } from '../model/verification-status.model';
import { ActivatedRoute } from '@angular/router';
import { ErrorResponse } from '../../shared/model/error.response.model';
import { RegisterService } from '../service/register.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { passwordValidator } from '../../infrastructure/validators/passwordValidator';
import { matchPasswordsValidator } from '../../infrastructure/validators/matchPasswordsValidator';
import zxcvbn from 'zxcvbn';

@Component({
  selector: 'app-activate-account-ca',
  standalone: false,
  templateUrl: './activate-account-ca.component.html',
  styleUrl: './activate-account-ca.component.css',
})
export class ActivateAccountCaComponent implements OnInit {
  form!: FormGroup;
  verificationStatus: VerificationStatus = VerificationStatus.LOADING;
  errorMessage: string = '';
  code: string = '';

  waitingResponse = false;
  showPassword = false;
  showConfirmPassword = false;
  passwordStrengthScore: number = 0;

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private registerService: RegisterService
  ) {}

  ngOnInit(): void {
    this.form = this.formBuilder.group(
      {
        password: ['', [Validators.required, passwordValidator()]],
        confirmPassword: ['', Validators.required],
      },
      { validators: matchPasswordsValidator('password', 'confirmPassword') }
    );
    this.route.queryParams.subscribe((params) => {
      this.code = params['code'];
      if (this.code === '') {
        this.verificationStatus = VerificationStatus.ERROR;
        this.errorMessage = 'No verification code provided.';
      } else {
        this.verificationStatus = VerificationStatus.PENDING;
      }
    });

    console.log(this.code);
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

  onSubmit() {
    if (this.form.valid) {
      this.verificationStatus = VerificationStatus.LOADING;
      this.waitingResponse = true;
      const formValues = this.form.value;
      this.verifyAccount(this.code, formValues.password);
    }
  }
  verifyAccount(activationCode: string, password: string): void {
    this.verificationStatus = VerificationStatus.LOADING;
    this.registerService.activateCaAccount(activationCode, password).subscribe({
      next: () => {
        this.verificationStatus = VerificationStatus.SUCCESS;
      },
      error: (error: ErrorResponse) => {
        this.verificationStatus = VerificationStatus.ERROR;
        this.errorMessage = error.message || 'Account activation failed.';
      },
    });
  }
}
