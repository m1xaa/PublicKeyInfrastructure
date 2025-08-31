import { Component, Input, Output, EventEmitter } from '@angular/core';
import { RegisterResponse } from '../model/register.response.model';

@Component({
  selector: 'app-register-success-dialog',
  templateUrl: './register-success-dialog.component.html',
})
export class RegisterSuccessDialogComponent {
  @Input() registerResponse?: RegisterResponse;
  @Input() isOpen?: boolean = false;
  @Output() isOpenChange = new EventEmitter<boolean>();

  goHome() {
    this.isOpenChange.emit();
    window.location.href = '/';
  }

  login() {
    this.isOpenChange.emit();
    window.location.href = '/user/login';
  }
}
