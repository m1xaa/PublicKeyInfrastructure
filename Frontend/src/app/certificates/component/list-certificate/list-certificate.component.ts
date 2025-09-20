import {Component} from '@angular/core';
import {CertificateResponseDTO} from '../../model/CertificateResponseDTO';
import {CertificateStatus} from '../../model/CertificateStatus';
import {CardCertificateComponent} from '../card-certificate/card-certificate.component';
import {NgForOf} from '@angular/common';
import {CertificateServiceService} from '../../../generate-certificate/service/certificate-service.service';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-list-certificate',
  standalone: true,
  imports: [
    CardCertificateComponent,
    NgForOf
  ],
  templateUrl: './list-certificate.component.html',
  styleUrl: './list-certificate.component.css'
})
export class ListCertificateComponent {
  certs: CertificateResponseDTO[] = []
  constructor(private certificateService:CertificateServiceService, private toast: ToastrService) {
  }

  ngOnInit(): void {
    this.getCertificates();
  }

  getCertificates(){
    this.certificateService.getAll().subscribe({
      next: (res) => {
        this.certs = res;
      },
      error: (err) => {
        this.toast.error(err.message, 'Error');
      }
    })
  }
}
