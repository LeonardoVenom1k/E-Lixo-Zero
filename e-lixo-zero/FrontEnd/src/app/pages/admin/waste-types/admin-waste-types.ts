import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
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

  wasteTypes: WasteType[] = [];
  editingId: number | null = null;
  showForm = false;
  message = '';
  error = '';

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
      next: (wasteTypes) => (this.wasteTypes = wasteTypes),
      error: () => (this.error = 'Erro ao carregar tipos de resíduo.'),
    });
  }

  startCreate(): void {
    this.clearMessages();
    this.editingId = null;
    this.showForm = true;
    this.form.reset();
  }

  startEdit(wasteType: WasteType): void {
    this.clearMessages();
    this.editingId = wasteType.id;
    this.showForm = true;
    this.form.patchValue({
      name: wasteType.name,
      category: wasteType.category,
      description: wasteType.description,
    });
  }

  cancelForm(): void {
    this.showForm = false;
    this.editingId = null;
    this.clearMessages();
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.message = '';
      this.error = 'Preencha os campos obrigatórios.';
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

    const request = this.editingId
      ? this.wasteTypesService.update(this.editingId, wasteType)
      : this.wasteTypesService.create(wasteType);

    request.subscribe({
      next: () => {
        this.message = `Resíduo ${this.editingId ? 'atualizado' : 'cadastrado'} com sucesso.`;
        this.showForm = false;
        this.editingId = null;
        this.load();
      },
      error: () => (this.error = 'Erro ao salvar resíduo.'),
    });
  }

  remove(wasteType: WasteType): void {
    if (!confirm(`Excluir o resíduo "${wasteType.name}"?`)) {
      return;
    }
    this.clearMessages();
    this.wasteTypesService.delete(wasteType.id).subscribe({
      next: () => {
        this.message = 'Resíduo excluído com sucesso.';
        this.load();
      },
      error: () => (this.error = 'Erro ao excluir resíduo. Ele pode estar vinculado a coletas.'),
    });
  }

  isFieldInvalid(name: string): boolean {
    const field = this.form.get(name);
    return !!field && field.invalid && field.touched;
  }

  private clearMessages(): void {
    this.message = '';
    this.error = '';
  }
}
