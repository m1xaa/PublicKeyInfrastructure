import { Component } from '@angular/core';
import { AuthService } from '../../infrastructure/service/auth.service';
import { map, Observable } from 'rxjs';
import { UserRole } from '../../infrastructure/auth/model/user-role.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: false,
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
})
export class NavbarComponent {
  isExpanded = false;

  constructor(private authService: AuthService, private router: Router) {}

  get loggedIn$(): Observable<boolean> {
    return this.authService.loggedIn$;
  }

  get isAdmin$(): Observable<boolean> {
    return this.authService.userRole$.pipe(
      map((role) => role === UserRole.Admin)
    );
  }

  get isRegularUser$(): Observable<boolean> {
    return this.authService.userRole$.pipe(
      map((role) => role === UserRole.Regular)
    );
  }

  get isCA$(): Observable<boolean> {
    return this.authService.userRole$.pipe(map((role) => role === UserRole.Ca));
  }

  public logOut() {
    this.authService.logOut();
    this.router.navigate(['/']).then(() => {
      window.location.reload();
    });
  }
}
