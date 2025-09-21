import { Component, OnInit } from '@angular/core';
import { CertificateServiceService } from '../../../generate-certificate/service/certificate-service.service';
import { AuthService } from '../../../infrastructure/service/auth.service';
import { CertificateResponseDTO } from '../../model/CertificateResponseDTO';
import { CardCertificateComponent } from "../card-certificate/card-certificate.component";
import { NgForOf, NgIf, NgTemplateOutlet } from '@angular/common';
import { RevocationReason } from '../../model/revocation-reason';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-certificate-homepage-overview',
  standalone: true,
  imports: [
    CardCertificateComponent,
    NgForOf,
    NgTemplateOutlet,
    NgIf
  ],
  templateUrl: './certificate-homepage-overview.component.html',
  styleUrl: './certificate-homepage-overview.component.css'
})
export class CertificateHomepageOverviewComponent implements OnInit {

  certificates: CertificateResponseDTO[] = [];

  constructor(
    private certificateService: CertificateServiceService,
    private authService: AuthService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadCertificates();
  }

  loadCertificates() {
    this.certificateService.getCertificateOverviewByUserId(this.authService.getUser()!.userId).subscribe({
      next: (certs) => {
        this.certificates = certs;
      }
    });
  }

  getChildren(parentId: string): CertificateResponseDTO[] {
    return this.certificates.filter(c => c.issuerId === parentId);
  }

  getRoots(): CertificateResponseDTO[] {
    return this.certificates.filter(c => !c.issuerId);
  }

  onRevoke(reason: RevocationReason, id: string) {
    this.certificateService.revokeCertificate(id, {reason: reason}).subscribe({
      next: () => {
        this.toastr.success("Certificate revoked successfully");
        this.loadCertificates();
      }
    });
  }
}
