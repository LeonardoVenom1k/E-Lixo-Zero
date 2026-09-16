import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';
import { Notification } from '../models/notification.model';

@Injectable({
  providedIn: 'root',
})
export class NotificationsService {
  private http = inject(HttpClient);
  private api = 'http://localhost:8087/api/notifications';

  list(): Observable<Notification[]> {
    return this.http.get<Notification[]>(this.api);
  }

  findById(id: number): Observable<Notification> {
    return this.http.get<Notification>(`${this.api}/${id}`);
  }

  listByUser(userId: number): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.api}/user/${userId}`);
  }

  listUnreadByUser(userId: number): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.api}/user/${userId}/unread`);
  }

  create(notification: Notification): Observable<Notification> {
    return this.http.post<Notification>(this.api, notification);
  }

  markAsRead(id: number): Observable<Notification> {
    return this.http.put<Notification>(`${this.api}/${id}/mark-read`, {});
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}