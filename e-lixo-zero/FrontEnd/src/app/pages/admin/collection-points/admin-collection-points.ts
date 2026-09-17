import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { CollectionPoint } from '../../../models/collection-point.model';
import { CollectionPointsService } from '../../../services/collection-points';

@Component({
  selector: 'app-admin-collection-points',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-collection-points.html',
  styleUrl: './admin-collection-points.scss',
})
export class AdminCollectionPoints implements OnInit {
  private fb = inject(FormBuilder);
  private collectionPointsService = inject(CollectionPointsService);

  points = signal<CollectionPoint[]>([]);
  editingId = signal<number | null>(null);
  showForm = signal(false);
  message = signal('');
  error = signal('');
  pendingDelete = signal<CollectionPoint | null>(null);

  form = this.fb.group({
    name: ['', Validators.required],
    street: ['', Validators.required],
    number: [''],
    neighborhood: [''],
    city: ['Santa Rita do Sapucaí', Validators.required],
    state: ['MG', Validators.required],
    phone: [''],
    openingHours: [''],
    latitude: [0, Validators.required],
    longitude: [0, Validators.required],
    acceptedWastes: [''],
  });

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.collectionPointsService.list().subscribe({
      next: (points) => this.points.set(points),
      error: () => this.error.set('Erro ao carregar pontos de coleta.'),
    });
  }

  startCreate(): void {
    this.clearMessages();
    this.editingId.set(null);
    this.showForm.set(true);
    this.form.reset({
      city: 'Santa Rita do Sapucaí',
      state: 'MG',
      latitude: 0,
      longitude: 0,
    });
  }

  startEdit(point: CollectionPoint): void {
    this.clearMessages();
    this.editingId.set(point.id);
    this.showForm.set(true);
    this.form.patchValue({
      name: point.name,
      street: point.street || '',
      number: point.number || '',
      neighborhood: point.neighborhood || '',
      city: point.city || 'Santa Rita do Sapucaí',
      state: point.state || 'MG',
      phone: point.phone || '',
      openingHours: point.openingHours || '',
      latitude: point.latitude,
      longitude: point.longitude,
      acceptedWastes: (point.acceptedWastes || []).join(', '),
    });
  }

  cancelForm(): void {
    this.showForm.set(false);
    this.editingId.set(null);
    this.clearMessages();
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.message.set('');
      this.error.set('Preencha os campos obrigatórios.');
      return;
    }

    this.clearMessages();
    const value = this.form.value;
    const point = {
      name: value.name || '',
      street: value.street || '',
      number: value.number || '',
      neighborhood: value.neighborhood || '',
      city: value.city || '',
      state: value.state || '',
      phone: value.phone || '',
      openingHours: value.openingHours || '',
      latitude: Number(value.latitude),
      longitude: Number(value.longitude),
      acceptedWastes: (value.acceptedWastes || '')
        .split(',')
        .map((waste) => waste.trim())
        .filter((waste) => waste.length > 0),
      active: true,
    } as CollectionPoint;

    const editingId = this.editingId();
    const request = editingId
      ? this.collectionPointsService.update(editingId, point)
      : this.collectionPointsService.create(point);

    request.subscribe({
      next: () => {
        this.message.set(`Ponto de coleta ${editingId ? 'atualizado' : 'cadastrado'} com sucesso.`);
        this.showForm.set(false);
        this.editingId.set(null);
        this.load();
      },
      error: () => this.error.set('Erro ao salvar ponto de coleta.'),
    });
  }

  remove(point: CollectionPoint): void {
    this.pendingDelete.set(point);
  }

  confirmDelete(): void {
    const point = this.pendingDelete();
    if (!point) {
      return;
    }
    this.pendingDelete.set(null);
    this.clearMessages();
    this.collectionPointsService.delete(point.id).subscribe({
      next: () => {
        this.message.set('Ponto de coleta excluído com sucesso.');
        this.load();
      },
      error: () => this.error.set('Erro ao excluir ponto de coleta.'),
    });
  }

  cancelDelete(): void {
    this.pendingDelete.set(null);
  }

  isFieldInvalid(name: string): boolean {
    const field = this.form.get(name);
    return !!field && field.invalid && field.touched;
  }

  private clearMessages(): void {
    this.message.set('');
    this.error.set('');
  }
}
