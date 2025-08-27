import { Component, OnInit } from '@angular/core';
import { VerificationStatus } from '../model/verification-status.model';
import { RegisterService } from '../service/register.service';
import { ActivatedRoute } from '@angular/router';
import { ErrorResponse } from '../../shared/model/error.response.model';

@Component({
  selector: 'app-activate-account',
  standalone: false,
  templateUrl: './activate-account.component.html',
  styleUrl: './activate-account.component.css',
})
export class ActivateAccountComponent implements OnInit {
  verificationStatus: VerificationStatus = VerificationStatus.LOADING;
  errorMessage: string = '';

  constructor(
    private route: ActivatedRoute,
    private registerService: RegisterService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      const code = params['code'];
      if (code) {
        this.verifyAccount(code);
      } else {
        this.verificationStatus = VerificationStatus.ERROR;
        this.errorMessage = 'No verification code provided.';
      }
    });
  }

  verifyAccount(activationCode: string): void {
    this.verificationStatus = VerificationStatus.LOADING;

    this.registerService.activateAccount(activationCode).subscribe({
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
