import { Routes } from '@angular/router';
import { RegisterComponent } from './user/register/register.component';
import { ActivateAccountComponent } from './user/activate-account/activate-account.component';
import { LoginComponent } from './user/login/login.component';
import { unauthenticatedGuard } from './infrastructure/auth/guard/unauthenticated.guard';
import { NotFoundComponent } from './layout/not-found/not-found/not-found.component';
import { FormCertificateComponent } from './generate-certificate/component/form-certificate/form-certificate.component';
import { authGuard } from './infrastructure/auth/guard/auth.guard';
import { HomePageComponent } from './home-page/home-page.component';
import { RegisterCaComponent } from './user/register-ca/register-ca.component';
import { ActivateAccountCaComponent } from './user/activate-account-ca/activate-account-ca.component';
import { ApproveUsersComponent } from './user/ca/approve-users/approve-users.component';
import {ListCertificateComponent} from './certificates/component/list-certificate/list-certificate.component';
import {DetailsCertificateComponent} from './certificates/component/details-certificate/details-certificate.component';

export const routes: Routes = [
  {
    path: '',
    component: HomePageComponent,
    canActivate: [authGuard],
  },
  {
    path: 'user/register',
    component: RegisterComponent,
    canActivate: [unauthenticatedGuard],
  },
  {
    path: 'user/register/ca',
    component: RegisterCaComponent,
    canActivate: [authGuard],
    data: { roles: ['ADMIN'] },
  },
  {
    path: 'user/activate',
    component: ActivateAccountComponent,
    canActivate: [unauthenticatedGuard],
  },
  {
    path: 'user/activate/ca',
    component: ActivateAccountCaComponent,
    canActivate: [unauthenticatedGuard],
  },
  {
    path: 'user/login',
    component: LoginComponent,
    canActivate: [unauthenticatedGuard],
  },
  {
    path: 'user/approve',
    component: ApproveUsersComponent,
    canActivate: [authGuard],
    data: { roles: ['CA'] },
  },
  {
    path: 'admin-panel',
    component: ListCertificateComponent,
    canActivate: [authGuard],
    data: { roles: ['ADMIN'] },
  },
  {
    path: 'certificate/:id',
    component: DetailsCertificateComponent,
    canActivate: [authGuard],
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
