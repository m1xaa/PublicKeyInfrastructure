import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CertificateResponseDTO } from '../../model/CertificateResponseDTO';
import { DatePipe, NgClass, NgForOf, NgIf, NgSwitch, NgSwitchCase } from '@angular/common';
import { LucideAngularModule } from 'lucide-angular';
import { Router, RouterLink } from '@angular/router';
import { RevocationReason } from '../../model/revocation-reason';
import { FormsModule } from '@angular/forms';
import { CertificateServiceService } from '../../../generate-certificate/service/certificate-service.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-card-certificate',
  standalone: true,
  imports: [
    NgClass,
    DatePipe,
    NgSwitch,
    NgSwitchCase,
    LucideAngularModule,
    RouterLink,
    NgIf,
    FormsModule,
    NgForOf
  ],
  templateUrl: './card-certificate.component.html',
  styleUrl: './card-certificate.component.css'
})
export class CardCertificateComponent {
  @Input() certificate!: CertificateResponseDTO;
  @Input() enableRevoking: boolean = false;
  @Output() onRevoke = new EventEmitter<RevocationReason>();

  showRevokeModal = false;
  selectedReason: RevocationReason | null = null;
  reasons = Object.values(RevocationReason);

  constructor(private router: Router) {}

  navigateToDetails() {
    this.router.navigate(['/certificate', this.certificate.id]);
  }

  openRevokeModal(event: Event) {
    event.stopPropagation(); // prevent card navigation
    this.showRevokeModal = true;
  }

  closeRevokeModal() {
    this.showRevokeModal = false;
    this.selectedReason = null;
  }

  confirmRevoke() {
    if (!this.selectedReason) return;
    this.onRevoke.emit(this.selectedReason);
    this.closeRevokeModal();
  }
}
