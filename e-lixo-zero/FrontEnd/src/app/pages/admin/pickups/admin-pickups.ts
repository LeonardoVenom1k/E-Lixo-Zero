import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { Pickup } from '../../../models/pickup.model';
import { PickupsService } from '../../../services/pickups';

@Component({
  selector: 'app-admin-pickups',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-pickups.html',
  styleUrl: './admin-pickups.scss',
})
export class AdminPickups implements OnInit {
  private pickupsService = inject(PickupsService);

  pickups = signal<Pickup[]>([]);
  search = signal('');
  message = signal('');
  error = signal('');

  readonly statuses = ['PENDING', 'Scheduled', 'In Progress', 'Completed', 'Cancelled'];

  filteredPickups = computed(() => {
    const term = this.search().trim().toLowerCase();
    if (!term) {
      return this.pickups();
    }
    return this.pickups().filter((pickup) =>
      [pickup.userName, pickup.waste, pickup.street, pickup.neighborhood, pickup.city, this.statusLabel(pickup.status)]
        .filter(Boolean)
        .some((field) => field!.toLowerCase().includes(term))
    );
  });

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.pickupsService.listAll().subscribe({
      next: (pickups) => this.pickups.set(pickups),
      error: () => this.error.set('Erro ao carregar agendamentos.'),
    });
  }

  onStatusChange(pickup: Pickup, event: Event): void {
    const select = event.target as HTMLSelectElement;
    const status = select.value;
    if (this.isFinalStatus(status)) {
      const confirmed = confirm(
        `Marcar a coleta de ${pickup.waste} como "${this.statusLabel(status)}"? Essa ação não poderá ser desfeita.`
      );
      if (!confirmed) {
        select.value = pickup.status;
        return;
      }
    }
    this.updateStatus(pickup, status);
  }

  updateStatus(pickup: Pickup, status: string): void {
    this.clearMessages();
    this.pickupsService.updateStatus(pickup.id, status).subscribe({
      next: () => {
        this.message.set('Status atualizado e usuário notificado.');
        this.load();
      },
      error: () => this.error.set('Erro ao atualizar status.'),
    });
  }

  isFinalStatus(status: string): boolean {
    return status === 'Completed' || status === 'Cancelled' || status === 'Canceled';
  }

  statusLabel(status: string): string {
    const labels: Record<string, string> = {
      'PENDING': 'Pendente',
      'Scheduled': 'Agendada',
      'In Progress': 'Em andamento',
      'Completed': 'Concluída',
      'Cancelled': 'Cancelada',
      'Canceled': 'Cancelada',
    };
    return labels[status] || status;
  }

  statusClass(status: string): string {
    const classes: Record<string, string> = {
      'PENDING': 'pending',
      'Scheduled': 'scheduled',
      'In Progress': 'in-progress',
      'Completed': 'completed',
      'Cancelled': 'cancelled',
      'Canceled': 'cancelled',
    };
    return classes[status] || '';
  }

  private clearMessages(): void {
    this.message.set('');
    this.error.set('');
  }
}
