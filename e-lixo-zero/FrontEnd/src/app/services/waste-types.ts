import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

import { WasteType } from '../models/waste-type.model';

@Injectable({
  providedIn: 'root',
})
export class WasteTypesService {
  private http = inject(HttpClient);

  private api = '/api/waste-types';

  list(): Observable<WasteType[]> {
    return this.http.get<WasteType[]>(this.api);
  }

  findById(id: number): Observable<WasteType> {
    return this.http.get<WasteType>(`${this.api}/${id}`);
  }

  findByCategory(category: string): Observable<WasteType[]> {
    return this.http.get<WasteType[]>(`${this.api}/category/${category}`);
  }

  create(waste: WasteType): Observable<WasteType> {
    return this.http.post<WasteType>(this.api, waste);
  }

  update(id: number, waste: WasteType): Observable<WasteType> {
    return this.http.put<WasteType>(`${this.api}/${id}`, waste);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}