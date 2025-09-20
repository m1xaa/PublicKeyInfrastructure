import {Component, Input} from '@angular/core';
import {CertificateDetailsDTO} from '../../model/CertificateDetailsDTO';
import {DatePipe} from '@angular/common';
import {LucideAngularModule} from 'lucide-angular';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-details-certificate',
  standalone: true,
  imports: [
    DatePipe,
    LucideAngularModule
  ],
  templateUrl: './details-certificate.component.html',
  styleUrl: './details-certificate.component.css'
})
export class DetailsCertificateComponent {
  @Input() certificate!: CertificateDetailsDTO;
  certificateId!: string;

  constructor(private route:ActivatedRoute) { }
  ngOnInit(): void {
    this.getCertificate();
  }
  getCertificate(){
    this.certificateId = this.route.snapshot.paramMap.get('id')!;
    console.log('Certificate ID:', this.certificateId);
  }
  downloadCertificate() {

  }
}
