import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';

import { Pickup } from '../../models/pickup.model';
import { PickupsService } from '../../services/pickups';

@Component({
  selector: 'app-my-pickups',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './my-pickups.html',
  styleUrl: './my-pickups.scss',
})
export class MyPickups  {
  private pickupsService = inject(PickupsService);

  pickups: Pickup[] = [];
  pickups$ = this.pickupsService.list()

  statusLabel(status: string): string {
    const labels: Record<string, string> = {
      'Scheduled': 'Agendada',
      'In Progress': 'Em andamento',
      'Completed': 'Concluída',
      'PENDING': 'Pendente',
      'Cancelled': 'Cancelada',
      'Canceled': 'Cancelada',
    };
    return labels[status] || status;
  }

}
