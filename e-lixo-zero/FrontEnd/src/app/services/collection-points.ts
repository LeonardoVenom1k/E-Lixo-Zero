import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { CollectionPoint } from '../models/collection-point.model';

@Injectable({
  providedIn: 'root',
})
export class CollectionPointsService {
  private http = inject(HttpClient);

  private api = 'http://localhost:8087/api/collection-points';

  list(): Observable<CollectionPoint[]> {
    return this.http.get<CollectionPoint[]>(this.api);
  }

  findById(id: number): Observable<CollectionPoint> {
    return this.http.get<CollectionPoint>(`${this.api}/${id}`);
  }

  searchByCity(city: string): Observable<CollectionPoint[]> {
    return this.http.get<CollectionPoint[]>(`${this.api}/city/${city}`);
  }

  nearby(lat: number, lng: number): Observable<CollectionPoint[]> {
    return this.http.get<CollectionPoint[]>(`${this.api}/nearby`, {
      params: { lat, lng },
    });
  }

  create(point: CollectionPoint): Observable<CollectionPoint> {
    return this.http.post<CollectionPoint>(this.api, point);
  }

  update(id: number, point: CollectionPoint): Observable<CollectionPoint> {
    return this.http.put<CollectionPoint>(`${this.api}/${id}`, point);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}