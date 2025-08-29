import { Injectable } from '@angular/core';
import { BehaviorSubject, map, Observable } from 'rxjs';
import { UserRole } from '../auth/model/user-role.model';
import { User } from '../auth/model/user.mode';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private user = new BehaviorSubject<User | null>(this.getStoredUser());
  private token = new BehaviorSubject<string | null>(this.getStoredToken());

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
    return this.token.getValue();
  }

  setJwt(token: string | null) {
    if (token) {
      localStorage.setItem('jwt', token);
    } else {
      localStorage.removeItem('jwt');
    }
    this.token.next(token);
  }

  private getStoredToken(): string | null {
    return localStorage.getItem('jwt');
  }

  logOut() {
    this.setUser(null);
    this.setJwt(null);
  }
}
