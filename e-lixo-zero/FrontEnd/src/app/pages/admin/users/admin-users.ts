import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';

import { User } from '../../../models/user.model';
import { UsersService } from '../../../services/users';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-users.html',
  styleUrl: './admin-users.scss',
})
export class AdminUsers implements OnInit {
  private usersService = inject(UsersService);

  users = signal<User[]>([]);
  message = signal('');
  error = signal('');
  pendingAction = signal<{ user: User; action: 'deactivate' | 'delete' } | null>(null);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.usersService.list().subscribe({
      next: (users) => this.users.set(users),
      error: () => this.error.set('Erro ao carregar usuários.'),
    });
  }

  toggleActive(user: User): void {
    if (user.active) {
      this.pendingAction.set({ user, action: 'deactivate' });
      return;
    }
    this.doToggleActive(user);
  }

  remove(user: User): void {
    this.pendingAction.set({ user, action: 'delete' });
  }

  confirmAction(): void {
    const pending = this.pendingAction();
    if (!pending) {
      return;
    }
    this.pendingAction.set(null);
    if (pending.action === 'delete') {
      this.doRemove(pending.user);
    } else {
      this.doToggleActive(pending.user);
    }
  }

  cancelAction(): void {
    this.pendingAction.set(null);
  }

  private doToggleActive(user: User): void {
    this.clearMessages();
    this.usersService.update(Number(user.id), { ...user, active: !user.active }).subscribe({
      next: () => {
        this.message.set(`Usuário ${user.active ? 'desativado' : 'ativado'} com sucesso.`);
        this.load();
      },
      error: () => this.error.set('Erro ao atualizar usuário.'),
    });
  }

  private doRemove(user: User): void {
    this.clearMessages();
    this.usersService.delete(Number(user.id)).subscribe({
      next: () => {
        this.message.set('Usuário excluído com sucesso.');
        this.load();
      },
      error: () => this.error.set('Erro ao excluir usuário. Ele pode ter coletas ou notificações vinculadas.'),
    });
  }

  typeLabel(userType?: string): string {
    return userType === 'ADMIN' ? 'Administrador' : 'Cidadão';
  }

  private clearMessages(): void {
    this.message.set('');
    this.error.set('');
  }
}
