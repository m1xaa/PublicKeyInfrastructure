import { Routes } from '@angular/router';
import { RegisterComponent } from './user/register/register.component';
import { ActivateAccountComponent } from './user/activate-account/activate-account.component';

export const routes: Routes = [
  { path: 'user/register', component: RegisterComponent },
  { path: 'user/activate', component: ActivateAccountComponent },
];
