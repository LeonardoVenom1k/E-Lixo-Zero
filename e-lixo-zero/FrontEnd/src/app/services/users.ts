import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root',
})
export class UsersService {
  private http = inject(HttpClient);
  private api = 'http://localhost:8087/api/users';

  list(): Observable<User[]> {
    return this.http.get<User[]>(this.api);
  }

  findById(id: number): Observable<User> {
    return this.http.get<User>(`${this.api}/${id}`);
  }

  create(user: User): Observable<User> {
    return this.http.post<User>(this.api, user);
  }

  update(id: number, user: User): Observable<User> {
    return this.http.put<User>(`${this.api}/${id}`, user);
  }

  login(email: string, password: string): Observable<User> {
    return this.http.post<User>(`${this.api}/login`, { email, password });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}