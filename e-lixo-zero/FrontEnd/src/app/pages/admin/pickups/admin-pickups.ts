import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
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

  pickups: Pickup[] = [];
  message = '';
  error = '';

  readonly statuses = ['PENDING', 'Scheduled', 'In Progress', 'Completed', 'Cancelled'];

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.pickupsService.listAll().subscribe({
      next: (pickups) => (this.pickups = pickups),
      error: () => (this.error = 'Erro ao carregar agendamentos.'),
    });
  }

  updateStatus(pickup: Pickup, status: string): void {
    this.clearMessages();
    this.pickupsService.updateStatus(pickup.id, status).subscribe({
      next: () => {
        this.message = 'Status atualizado e usuário notificado.';
        this.load();
      },
      error: () => (this.error = 'Erro ao atualizar status.'),
    });
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
    this.message = '';
    this.error = '';
  }
}
