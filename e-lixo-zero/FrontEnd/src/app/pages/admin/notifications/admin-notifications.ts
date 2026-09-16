import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { User } from '../../../models/user.model';
import { NotificationsService } from '../../../services/notifications';
import { UsersService } from '../../../services/users';

@Component({
  selector: 'app-admin-notifications',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-notifications.html',
  styleUrl: './admin-notifications.scss',
})
export class AdminNotifications implements OnInit {
  private fb = inject(FormBuilder);
  private notificationsService = inject(NotificationsService);
  private usersService = inject(UsersService);

  users = signal<User[]>([]);
  message = signal('');
  error = signal('');

  form = this.fb.group({
    recipient: ['all', Validators.required],
    title: ['', Validators.required],
    message: ['', Validators.required],
    notificationType: ['INFO', Validators.required],
  });

  ngOnInit(): void {
    this.usersService.list().subscribe({
      next: (users) => this.users.set(users.filter((user) => user.userType !== 'ADMIN')),
      error: () => this.error.set('Erro ao carregar usuários.'),
    });
  }

  send(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.message.set('');
      this.error.set('Preencha os campos obrigatórios.');
      return;
    }

    this.clearMessages();
    const value = this.form.value;
    const notification = {
      title: value.title || '',
      message: value.message || '',
      notificationType: value.notificationType || 'INFO',
      read: false,
    };

    if (value.recipient === 'all') {
      this.notificationsService.broadcast(notification).subscribe({
        next: (sent) => {
          this.message.set(`Notificação enviada para ${sent} usuário(s).`);
          this.resetForm();
        },
        error: () => this.error.set('Erro ao enviar notificação.'),
      });
      return;
    }

    this.notificationsService
      .create({ ...notification, userId: Number(value.recipient) })
      .subscribe({
        next: () => {
          this.message.set('Notificação enviada com sucesso.');
          this.resetForm();
        },
        error: () => this.error.set('Erro ao enviar notificação.'),
      });
  }

  isFieldInvalid(name: string): boolean {
    const field = this.form.get(name);
    return !!field && field.invalid && field.touched;
  }

  private resetForm(): void {
    this.form.reset({
      recipient: 'all',
      notificationType: 'INFO',
    });
  }

  private clearMessages(): void {
    this.message.set('');
    this.error.set('');
  }
}
