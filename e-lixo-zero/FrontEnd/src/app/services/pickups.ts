import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

import { Pickup } from '../models/pickup.model';

@Injectable({
  providedIn: 'root',
})
export class PickupsService {
  private http = inject(HttpClient);

  private api = 'http://localhost:8087/api/pickups';

  list(): Observable<Pickup[]> {
    return this.http.get<Pickup[]>(this.api);
  }

  listAll(): Observable<Pickup[]> {
    return this.http.get<Pickup[]>(`${this.api}/all`);
  }

  listForCollector(): Observable<Pickup[]> {
    return this.http.get<Pickup[]>(`${this.api}/collector`);
  }

  create(pickup: Omit<Pickup, 'id'>): Observable<Pickup> {
    return this.http.post<Pickup>(this.api, pickup);
  }

  findById(id: number) {
  return this.http.get<Pickup>(`${this.api}/${id}`);
}

  updateStatus(id: number, status: string): Observable<Pickup> {
    return this.http.put<Pickup>(`${this.api}/${id}/status`, { status });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}