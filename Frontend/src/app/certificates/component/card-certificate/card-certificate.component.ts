import {Component, Input} from '@angular/core';
import {CertificateResponseDTO} from '../../model/CertificateResponseDTO';
import {DatePipe, NgClass, NgSwitch, NgSwitchCase, TitleCasePipe} from '@angular/common';
import {CertificateStatus} from '../../model/CertificateStatus';
import {LucideAngularModule} from 'lucide-angular';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-card-certificate',
  standalone: true,
  imports: [
    NgClass,
    DatePipe,
    NgSwitch,
    LucideAngularModule,
    NgSwitchCase,
    RouterLink
  ],
  templateUrl: './card-certificate.component.html',
  styleUrl: './card-certificate.component.css'
})
export class CardCertificateComponent {

  @Input() certificate!: CertificateResponseDTO;

  public constructor() {
  }

  ngOnInit(): void {
  }

}
