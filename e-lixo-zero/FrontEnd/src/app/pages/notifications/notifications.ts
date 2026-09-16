import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';

import { Notification } from '../../models/notification.model';
import { NotificationsService } from '../../services/notifications';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notifications.html',
  styleUrl: './notifications.scss',
})
export class Notifications {
  private notificationsService = inject(NotificationsService);

  notifications$ = this.notificationsService.list()

}