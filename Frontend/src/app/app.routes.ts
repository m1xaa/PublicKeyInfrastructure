import { Routes } from '@angular/router';
import { RegisterComponent } from './user/register/register.component';
import { ActivateAccountComponent } from './user/activate-account/activate-account.component';
import { LoginComponent } from './user/login/login.component';
import { unauthenticatedGuard } from './infrastructure/auth/guard/unauthenticated.guard';
import { NotFoundComponent } from './layout/not-found/not-found/not-found.component';

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
    path: '**',
    component: NotFoundComponent,
  },
];
