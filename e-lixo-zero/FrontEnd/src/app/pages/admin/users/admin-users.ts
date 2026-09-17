import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { User } from '../../../models/user.model';
import { UsersService } from '../../../services/users';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-users.html',
  styleUrl: './admin-users.scss',
})
export class AdminUsers implements OnInit {
  private usersService = inject(UsersService);

  users = signal<User[]>([]);
  search = signal('');
  message = signal('');
  error = signal('');
  pendingAction = signal<{ user: User; action: 'deactivate' | 'delete' | 'type'; newType?: string; select?: HTMLSelectElement } | null>(null);

  filteredUsers = computed(() => {
    const term = this.search().trim().toLowerCase();
    if (!term) {
      return this.users();
    }
    return this.users().filter((user) =>
      [user.fullName, user.email, user.city, user.neighborhood, user.phone, this.typeLabel(user.userType), user.active ? 'ativo' : 'inativo']
        .filter(Boolean)
        .some((field) => field!.toLowerCase().includes(term))
    );
  });

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.usersService.list().subscribe({
      next: (users) => this.users.set(users),
      error: () => this.error.set('Erro ao carregar usuários.'),
    });
  }

  onTypeChange(user: User, event: Event): void {
    const select = event.target as HTMLSelectElement;
    const newType = select.value;
    if (newType === user.userType) {
      return;
    }
    this.pendingAction.set({ user, action: 'type', newType, select });
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
    } else if (pending.action === 'type') {
      this.doChangeType(pending.user, pending.newType!);
    } else {
      this.doToggleActive(pending.user);
    }
  }

  cancelAction(): void {
    const pending = this.pendingAction();
    if (pending?.action === 'type' && pending.select) {
      pending.select.value = pending.user.userType ?? 'CITIZEN';
    }
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

  private doChangeType(user: User, newType: string): void {
    this.clearMessages();
    this.usersService.update(Number(user.id), { ...user, userType: newType }).subscribe({
      next: () => {
        this.message.set(`Perfil de ${user.fullName} alterado para ${this.typeLabel(newType)}.`);
        this.load();
      },
      error: () => this.error.set('Erro ao alterar perfil do usuário.'),
    });
  }

  typeLabel(userType?: string): string {
    if (userType === 'ADMIN') {
      return 'Administrador';
    }
    if (userType === 'COLLECTOR') {
      return 'Coletor';
    }
    return 'Cidadão';
  }

  private clearMessages(): void {
    this.message.set('');
    this.error.set('');
  }
}
