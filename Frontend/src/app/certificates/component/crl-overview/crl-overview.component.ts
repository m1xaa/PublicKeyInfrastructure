import { Component } from '@angular/core';
import { CertificateServiceService } from '../../../generate-certificate/service/certificate-service.service';
import { CertificateRevocationResponseDTO } from '../../model/certificate-revocation-response-dto';
import { NgForOf } from '@angular/common';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-crl-overview',
  standalone: true,
  imports: [
    NgForOf
  ],
  templateUrl: './crl-overview.component.html',
  styleUrl: './crl-overview.component.css'
})
export class CrlOverviewComponent {

  revocations: CertificateRevocationResponseDTO[] = [];

  constructor(
    private certificateService: CertificateServiceService,
    private toast: ToastrService
  ) {}

  ngOnInit() {
    this.certificateService.getAllRevocations().subscribe({
      next: (response) => {
        this.revocations = response;
        console.log(this.revocations);
      }
    });
  }

  downloadCrl(id: string) {
    this.certificateService.downloadCrl(id)
      .subscribe({
        next: (blob: Blob) => {
          const a = document.createElement('a');
          a.href = URL.createObjectURL(blob);
          a.download = 'revocation.crl';
          a.click();
          URL.revokeObjectURL(a.href);
        },
        error: err => this.toast.error(err.message, 'Error')
      });
  }
} 
