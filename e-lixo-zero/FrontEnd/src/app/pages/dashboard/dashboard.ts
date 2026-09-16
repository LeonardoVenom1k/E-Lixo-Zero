import { Component, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';

import { PickupsService } from '../../services/pickups';
import { User, UserCompat } from '../../models/user.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard {
  private pickupsService = inject(PickupsService);

  user: UserCompat | null = (() => {
    const savedUser = localStorage.getItem('loggedInUser');
    if (!savedUser) return null;
    
    const user = JSON.parse(savedUser);
    // Convert fullName to name for compatibility
    return {
      ...user,
      name: user.fullName || user.name || 'User'
    };
  })();

  pickups = toSignal(
    this.pickupsService.list(),
    { initialValue: [] }
  );

  totalScheduleds = computed(
    () => this.pickups().filter(c => c.status === 'Scheduled').length
  );

  totalInProgress = computed(
    () => this.pickups().filter(c => c.status === 'In Progress').length
  );

  totalCompleted = computed(
    () => this.pickups().filter(c => c.status === 'Completed').length
  );

  totalWaste = computed(
    () => this.pickups().reduce(
      (total, pickup) => total + Number(pickup.quantity),
      0
    )
  );
}