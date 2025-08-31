import { Component } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';
import { UserModule } from './user/user-module';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { LayoutModule } from './layout/layout.module';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterModule, UserModule, HttpClientModule, LayoutModule],
  providers: [HttpClient],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  title = 'Frontend';
}
