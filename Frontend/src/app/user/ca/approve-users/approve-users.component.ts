import { Component, OnInit } from '@angular/core';
import { CaService } from '../../service/ca.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-approve-users',
  standalone: false,
  templateUrl: './approve-users.component.html',
  styleUrl: './approve-users.component.css',
})
export class ApproveUsersComponent implements OnInit {
  constructor(
    private caService: CaService,
    private toastService: ToastrService
  ) {}

  pendingUsers: any[] = [];
  totalPages: number = 2;
  currentPage: number = 1;

  loadPrev() {
    if (this.currentPage - 1 >= 0) {
      this.currentPage--;
      this.loadPendingUsers(this.totalPages);
    }
  }

  loadNext() {
    if (this.currentPage + 1 <= this.totalPages) {
      this.currentPage++;
      this.loadPendingUsers(this.totalPages);
    }
  }

  loadPendingUsers(page: number = 0) {
    this.caService.getPendingUsers(page).subscribe((res: any) => {
      this.pendingUsers = res.content;
      this.totalPages = res.totalPages;
      this.currentPage = res.number;
      console.log(res);
    });
  }

  ngOnInit() {
    this.loadPendingUsers();
  }

  rejectUser(email: string) {
    this.caService.rejectUser(email).subscribe({
      next: (res) => {
        if (res) {
          this.loadPendingUsers();
          this.toastService.success('You rejected user', 'Reject');
        }
      },
    });
  }
  acceptUser(email: string) {
    this.caService.acceptUser(email).subscribe({
      next: (res) => {
        if (res) {
          this.loadPendingUsers();
          this.toastService.success('You accepted user', 'Accept');
        }
      },
    });
  }
}
