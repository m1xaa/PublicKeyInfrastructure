import { Component } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';
import { UserModule } from './user/user-module';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterModule, UserModule, HttpClientModule],
  providers: [HttpClient],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  title = 'Frontend';
}
