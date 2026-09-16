import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { WasteType } from '../../../models/waste-type.model';
import { WasteTypesService } from '../../../services/waste-types';

@Component({
  selector: 'app-admin-waste-types',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-waste-types.html',
  styleUrl: './admin-waste-types.scss',
})
export class AdminWasteTypes implements OnInit {
  private fb = inject(FormBuilder);
  private wasteTypesService = inject(WasteTypesService);

  wasteTypes = signal<WasteType[]>([]);
  editingId = signal<number | null>(null);
  showForm = signal(false);
  message = signal('');
  error = signal('');

  form = this.fb.group({
    name: ['', Validators.required],
    category: ['', Validators.required],
    description: [''],
  });

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.wasteTypesService.list().subscribe({
      next: (wasteTypes) => this.wasteTypes.set(wasteTypes),
      error: () => this.error.set('Erro ao carregar tipos de resíduo.'),
    });
  }

  startCreate(): void {
    this.clearMessages();
    this.editingId.set(null);
    this.showForm.set(true);
    this.form.reset();
  }

  startEdit(wasteType: WasteType): void {
    this.clearMessages();
    this.editingId.set(wasteType.id);
    this.showForm.set(true);
    this.form.patchValue({
      name: wasteType.name,
      category: wasteType.category,
      description: wasteType.description,
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
    const wasteType = {
      name: value.name || '',
      category: value.category || '',
      description: value.description || '',
      active: true,
    } as WasteType;

    const editingId = this.editingId();
    const request = editingId
      ? this.wasteTypesService.update(editingId, wasteType)
      : this.wasteTypesService.create(wasteType);

    request.subscribe({
      next: () => {
        this.message.set(`Resíduo ${editingId ? 'atualizado' : 'cadastrado'} com sucesso.`);
        this.showForm.set(false);
        this.editingId.set(null);
        this.load();
      },
      error: () => this.error.set('Erro ao salvar resíduo.'),
    });
  }

  remove(wasteType: WasteType): void {
    if (!confirm(`Excluir o resíduo "${wasteType.name}"?`)) {
      return;
    }
    this.clearMessages();
    this.wasteTypesService.delete(wasteType.id).subscribe({
      next: () => {
        this.message.set('Resíduo excluído com sucesso.');
        this.load();
      },
      error: () => this.error.set('Erro ao excluir resíduo. Ele pode estar vinculado a coletas.'),
    });
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
