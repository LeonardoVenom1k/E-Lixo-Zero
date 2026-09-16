import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { CollectionPointsService } from '../../services/collection-points';
import { CollectionPoint } from '../../models/collection-point.model';

@Component({
  selector: 'app-collection-points',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './collection-points.html',
  styleUrl: './collection-points.scss',
})
export class CollectionPointsComponent  {
  private collectionPointsService = inject(CollectionPointsService);

  term = '';
  locating = signal(false);
  locationError = signal('');
  points = signal<CollectionPoint[]>([]);

  constructor() {
    this.collectionPointsService.list().subscribe(points => this.points.set(points));
  }

  search(): void {
    const city = this.term.trim();
    const source = city
      ? this.collectionPointsService.searchByCity(city)
      : this.collectionPointsService.list();
    source.subscribe(points => this.points.set(points));
  }

  useMyLocation(): void {
    this.locationError.set('');
    if (!navigator.geolocation) {
      this.locationError.set('Seu navegador não suporta geolocalização.');
      return;
    }
    this.locating.set(true);
    navigator.geolocation.getCurrentPosition(
      position => {
        this.locating.set(false);
        this.collectionPointsService
          .nearby(position.coords.latitude, position.coords.longitude)
          .subscribe(points => this.points.set(points));
      },
      () => {
        this.locating.set(false);
        this.locationError.set('Não foi possível obter sua localização. Verifique a permissão do navegador.');
      }
    );
  }
}
