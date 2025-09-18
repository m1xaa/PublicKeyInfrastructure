import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RegisterComponent } from './register/register.component';
import { HttpClientModule } from '@angular/common/http';
import { RegisterSuccessDialogComponent } from './register-success-dialog/register-success-dialog.component';
import { ActivateAccountComponent } from './activate-account/activate-account.component';
import { LoginComponent } from './login/login.component';
import { RouterLink } from '@angular/router';
import { RegisterCaComponent } from './register-ca/register-ca.component';
import { ActivateAccountCaComponent } from './activate-account-ca/activate-account-ca.component';
import { ApproveUsersComponent } from './ca/approve-users/approve-users.component';

@NgModule({
  declarations: [
    RegisterComponent,
    RegisterSuccessDialogComponent,
    ActivateAccountComponent,
    LoginComponent,
    RegisterCaComponent,
    ActivateAccountCaComponent,
    ApproveUsersComponent,
  ],
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule, RouterLink],
  exports: [RouterLink],
})
export class UserModule {}
