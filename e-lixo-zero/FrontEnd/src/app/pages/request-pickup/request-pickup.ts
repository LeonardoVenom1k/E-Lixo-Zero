import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { toSignal } from '@angular/core/rxjs-interop';

import { WasteType } from '../../models/waste-type.model';
import { PickupsService } from '../../services/pickups';
import { WasteTypesService } from '../../services/waste-types';

function notPastDate(control: AbstractControl): ValidationErrors | null {
  const valor = control.value;
  if (!valor) {
    return null;
  }
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const date = new Date(`${valor}T00:00:00`);
  return date < today ? { pastDate: true } : null;
}

@Component({
  selector: 'app-request-pickup',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './request-pickup.html',
  styleUrl: './request-pickup.scss',
})
export class RequestPickup {
  private fb = inject(FormBuilder);
  private pickupsService = inject(PickupsService);
  private wasteTypesService = inject(WasteTypesService);

  wasteTypes = toSignal(this.wasteTypesService.list(), {
    initialValue: [] as WasteType[],
  });

  message = '';
  error = '';

  today = (() => {
    const agora = new Date();
    const mes = String(agora.getMonth() + 1).padStart(2, '0');
    const dia = String(agora.getDate()).padStart(2, '0');
    return `${agora.getFullYear()}-${mes}-${dia}`;
  })();

  form = this.fb.group({
    waste: ['', Validators.required],
    quantity: [1, [Validators.required, Validators.min(1)]],
    street: ['', Validators.required],
    number: ['', Validators.required],
    neighborhood: ['', Validators.required],
    city: ['Santa Rita do Sapucaí', Validators.required],
    date: ['', [Validators.required, notPastDate]],
    period: ['Manhã', Validators.required],
  });

  isFieldInvalid(name: string): boolean {
    const field = this.form.get(name);
    return !!field && field.invalid && field.touched;
  }

  fieldError(name: string): string {
    const field = this.form.get(name);
    if (!field || !field.touched || !field.errors) {
      return '';
    }
    if (field.errors['required']) {
      return 'Campo obrigatório.';
    }
    if (field.errors['pastDate']) {
      return 'A data não pode estar no passado.';
    }
    if (field.errors['min']) {
      return 'A quantidade mínima é 1.';
    }
    return 'Valor inválido.';
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.message = '';
      this.error = 'Verifique os campos destacados antes de agendar.';
      return;
    }

    this.message = '';
    this.error = '';

    const pickup = {
      ...this.form.value,
      status: 'Scheduled',
    };

    this.pickupsService.create(pickup as any).subscribe({
      next: (response) => {
        console.log('Pickup created successfully:', response);
        this.message = 'Coleta agendada com sucesso!';

        this.form.reset({
          quantity: 1,
          city: 'Santa Rita do Sapucaí',
          period: 'Manhã',
        });
      },
      error: (error) => {
        console.error('Error scheduling pickup:', error);
        this.error = 'Erro ao agendar coleta. Tente novamente.';
      },
    });
  }
}
