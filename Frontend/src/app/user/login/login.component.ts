import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { ErrorResponse } from '../../shared/model/error.response.model';
import { finalize } from 'rxjs';
import { LoginService } from '../service/login.service';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  form!: FormGroup;
  showPassword = false;
  waitingResponse = false;
  redirectUrl!: string;

  constructor(
    private fb: FormBuilder,
    private loginService: LoginService,
    private router: Router,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
    });
  }

  get email() {
    return this.form.get('email');
  }

  get password() {
    return this.form.get('password');
  }

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  async onSubmit() {
    if (this.form.valid) {
      this.waitingResponse = true;
      this.loginService
        .login(this.email?.value, this.password?.value)
        .pipe(finalize(() => (this.waitingResponse = false)))
        .subscribe({
          next: () => {
            this.toastr.success("You've successfully logged in!", 'Success');
            this.router.navigateByUrl('/');
          },
          error: (err: ErrorResponse) => {
            this.toastr.error(err.message, 'Oops!');
          },
        });
    }
  }
}
