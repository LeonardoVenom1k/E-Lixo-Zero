import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { map, catchError } from 'rxjs';
import { of } from 'rxjs';

import { User, UserCompat } from '../models/user.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private api = 'http://localhost:8087/api/users';
  private userKey = 'loggedInUser';
  private tokenKey = 'token';

  login(email: string, password: string) {
    return this.http.post<any>(`${this.api}/login`, { email, password }).pipe(
      map((user) => {
        console.log('User received from backend:', user);

        const userCompat: UserCompat = {
          id: String(user.id),
          name: user.fullName,
          email: user.email,
          password: '',
          street: user.street || '',
          number: user.number || '',
          neighborhood: user.neighborhood || '',
          city: user.city || '',
          userType: user.userType || 'CITIZEN'
        };

        localStorage.setItem(this.userKey, JSON.stringify(userCompat));

        if (user.token) {
          localStorage.setItem(this.tokenKey, user.token);
        }

        console.log('User saved to localStorage:', userCompat);
        return true;
      }),
      catchError((error) => {
        console.error('Login error:', error);
        console.error('Status:', error.status);
        console.error('Message:', error.message);
        return of(false);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.userKey);
    localStorage.removeItem(this.tokenKey);
  }

  isLoggedIn(): boolean {
    return localStorage.getItem(this.userKey) !== null;
  }

  getUser(): UserCompat | null {
    const savedUser = localStorage.getItem(this.userKey);
    return savedUser ? JSON.parse(savedUser) : null;
  }

  isAdmin(): boolean {
    return this.getUser()?.userType === 'ADMIN';
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  register(user: Omit<User, 'id'>) {
    return this.http.post<User>(this.api, user);
  }
}