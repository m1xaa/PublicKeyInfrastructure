import { Injectable } from '@angular/core';
import { BehaviorSubject, map, Observable } from 'rxjs';
import { UserRole } from '../auth/model/user-role.model';
import { User } from '../auth/model/user.mode';
import { HttpClient } from '@angular/common/http';
import { RefreshTokenResponse } from '../auth/model/refresh-token.response';
import { environment } from '../../environment/environment';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private httpClient: HttpClient) {}

  private user = new BehaviorSubject<User | null>(this.getStoredUser());
  private jwtToken = new BehaviorSubject<string | null>(
    this.getStoredJwtToken()
  );
  private refreshToken = new BehaviorSubject<string | null>(
    this.getStoredRefreshTokenToken()
  );

  user$: Observable<User | null> = this.user.asObservable();

  loggedIn$: Observable<boolean> = this.user$.pipe(
    map((user) => user !== null)
  );

  userRole$: Observable<UserRole> = this.user$.pipe(
    map((user) => user?.role ?? UserRole.Unauthenticated)
  );

  getUser(): User | null {
    return this.user.getValue();
  }

  setUser(user: User | null) {
    if (user) {
      localStorage.setItem('user', JSON.stringify(user));
    } else {
      localStorage.removeItem('user');
    }
    this.user.next(user);
  }

  private getStoredUser(): User | null {
    const storedUser = localStorage.getItem('user');
    if (!storedUser) return null;
    return JSON.parse(storedUser);
  }

  getJwt(): string | null {
    return this.jwtToken.getValue();
  }

  getRefreshToken(): string | null {
    return this.refreshToken.getValue();
  }

  setJwt(token: string | null) {
    if (token) {
      localStorage.setItem('jwt', token);
    } else {
      localStorage.removeItem('jwt');
    }
    this.jwtToken.next(token);
  }

  setRefreshToken(token: string | null) {
    if (token) {
      localStorage.setItem('refreshToken', token);
    } else {
      localStorage.removeItem('refreshToken');
    }
    this.refreshToken.next(token);
  }

  private getStoredJwtToken(): string | null {
    return localStorage.getItem('jwt');
  }

  private getStoredRefreshTokenToken(): string | null {
    return localStorage.getItem('refreshToken');
  }

  refreshJwt(refreshToken: string) {
    return this.httpClient.post<RefreshTokenResponse>(
      environment.apiHost + '/api/refresh',
      {
        refreshToken,
      }
    );
  }

  logOut() {
    this.setUser(null);
    this.setJwt(null);
    this.setRefreshToken(null);
  }
}
