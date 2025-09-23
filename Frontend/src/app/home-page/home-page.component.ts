import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CertificateHomepageOverviewComponent } from "../certificates/component/certificate-homepage-overview/certificate-homepage-overview.component";
import { AuthService } from '../infrastructure/service/auth.service';
import { UserRole } from '../infrastructure/auth/model/user-role.model';
import { NgIf } from '@angular/common';
import { User } from 'lucide-angular';

@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [RouterLink, CertificateHomepageOverviewComponent, NgIf],
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.css',
})
export class HomePageComponent {

  constructor(private authService: AuthService) {
  }

  isRegularUser() {
    return this.authService.getUser()?.role === UserRole.Regular;
  }

}
