import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { Pickup } from '../../../models/pickup.model';
import { PickupsService } from '../../../services/pickups';
import { AuthService } from '../../../services/auth';

@Component({
  selector: 'app-collector-pickups',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './collector-pickups.html',
  styleUrl: './collector-pickups.scss',
})
export class CollectorPickups implements OnInit {
  private pickupsService = inject(PickupsService);
  private auth = inject(AuthService);

  private myId = Number(this.auth.getUser()?.id) || 0;

  pickups = signal<Pickup[]>([]);
  search = signal('');
  statusFilter = signal('');
  showHidden = signal(false);
  message = signal('');
  error = signal('');
  pendingConfirmation = signal<{ pickup: Pickup; status: string; select: HTMLSelectElement } | null>(null);

  readonly statuses = ['PENDING', 'Scheduled', 'In Progress', 'Completed', 'Cancelled'];

  hiddenCount = computed(
    () => this.pickups().filter((pickup) => this.isFinalStatus(pickup.status)).length
  );

  filteredPickups = computed(() => {
    const status = this.statusFilter();
    let visible = this.pickups();
    if (status) {
      visible = visible.filter((pickup) => this.matchesStatus(pickup, status));
    } else if (!this.showHidden()) {
      visible = visible.filter((pickup) => !this.isFinalStatus(pickup.status));
    }
    const term = this.search().trim().toLowerCase();
    if (!term) {
      return visible;
    }
    return visible.filter((pickup) =>
      [pickup.userName, pickup.waste, pickup.street, pickup.neighborhood, pickup.city, this.statusLabel(pickup.status)]
        .filter(Boolean)
        .some((field) => field!.toLowerCase().includes(term))
    );
  });

  isMine(pickup: Pickup): boolean {
    return pickup.collectorId === this.myId;
  }

  toggleHidden(): void {
    this.showHidden.set(!this.showHidden());
  }

  private matchesStatus(pickup: Pickup, status: string): boolean {
    if (status === 'Cancelled') {
      return pickup.status === 'Cancelled' || pickup.status === 'Canceled';
    }
    return pickup.status === status;
  }

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.pickupsService.listForCollector().subscribe({
      next: (pickups) => this.pickups.set(pickups),
      error: () => this.error.set('Erro ao carregar coletas.'),
    });
  }

  onStatusChange(pickup: Pickup, event: Event): void {
    const select = event.target as HTMLSelectElement;
    const status = select.value;
    if (this.isFinalStatus(status)) {
      this.pendingConfirmation.set({ pickup, status, select });
      return;
    }
    this.updateStatus(pickup, status);
  }

  confirmStatusChange(): void {
    const pending = this.pendingConfirmation();
    if (!pending) {
      return;
    }
    this.pendingConfirmation.set(null);
    this.updateStatus(pending.pickup, pending.status);
  }

  cancelStatusChange(): void {
    const pending = this.pendingConfirmation();
    if (pending) {
      pending.select.value = pending.pickup.status;
    }
    this.pendingConfirmation.set(null);
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
