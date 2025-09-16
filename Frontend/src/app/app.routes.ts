import { Routes } from '@angular/router';
import { RegisterComponent } from './user/register/register.component';
import { ActivateAccountComponent } from './user/activate-account/activate-account.component';
import { LoginComponent } from './user/login/login.component';
import { unauthenticatedGuard } from './infrastructure/auth/guard/unauthenticated.guard';
import { NotFoundComponent } from './layout/not-found/not-found/not-found.component';
import {ListCertificateComponent} from './generate-certificate/component/list-certificate/list-certificate.component';
import {FormCertificateComponent} from './generate-certificate/component/form-certificate/form-certificate.component';
import {authGuard} from './infrastructure/auth/guard/auth.guard';

export const routes: Routes = [
  {
    path: 'user/register',
    component: RegisterComponent,
    canActivate: [unauthenticatedGuard],
  },
  {
    path: 'user/activate',
    component: ActivateAccountComponent,
    canActivate: [unauthenticatedGuard],
  },
  {
    path: 'user/login',
    component: LoginComponent,
    canActivate: [unauthenticatedGuard],
  },
  {
    path: 'admin-panel',
    component: ListCertificateComponent,
    canActivate: [unauthenticatedGuard],
  },
  {
    path: 'certificates',
    component: FormCertificateComponent,
    canActivate: [authGuard],
  },
  {
    path: '**',
    component: NotFoundComponent,
  },
];
