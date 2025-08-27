import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RegisterComponent } from './register/register.component';
import { HttpClientModule } from '@angular/common/http';
import { RegisterSuccessDialogComponent } from './register-success-dialog/register-success-dialog.component';
import { ActivateAccountComponent } from './activate-account/activate-account.component';

@NgModule({
  declarations: [
    RegisterComponent,
    RegisterSuccessDialogComponent,
    ActivateAccountComponent,
  ],
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule],
})
export class UserModule {}
