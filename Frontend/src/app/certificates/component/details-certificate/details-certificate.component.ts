import {Component, Input} from '@angular/core';
import {CertificateDetailsDTO} from '../../model/CertificateDetailsDTO';
import { DatePipe, NgIf } from '@angular/common';
import {LucideAngularModule} from 'lucide-angular';
import {ActivatedRoute} from '@angular/router';
import {CertificateServiceService} from '../../../generate-certificate/service/certificate-service.service';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-details-certificate',
  standalone: true,
  imports: [
    DatePipe,
    LucideAngularModule,
    NgIf
],
  templateUrl: './details-certificate.component.html',
  styleUrl: './details-certificate.component.css'
})
export class DetailsCertificateComponent {
  @Input() certificate!: CertificateDetailsDTO;
  certificateId!: string;

  constructor(private route:ActivatedRoute, private certificateService:CertificateServiceService, private toast: ToastrService) { }
  ngOnInit(): void {
    this.getCertificate();
  }
  getCertificate(){
    this.certificateId = this.route.snapshot.paramMap.get('id')!;
    this.certificateService.getCertificateDetails(this.certificateId).subscribe({
      next: (res) => {
        console.log(res);
        this.certificate = res;
      },
      error: (err) => {
        this.toast.error(err.message, 'Error');
      }
    }
    )
  }
  downloadCertificate() {
    this.certificateService.downloadKeyStore(this.certificateId)
      .subscribe({
        next: (blob: Blob) => {
          const a = document.createElement('a');
          a.href = URL.createObjectURL(blob);
          a.download = 'keystore.jks';
          a.click();
          URL.revokeObjectURL(a.href);
        },
        error: err => this.toast.error(err.message, 'Error')
      });
  }

}
