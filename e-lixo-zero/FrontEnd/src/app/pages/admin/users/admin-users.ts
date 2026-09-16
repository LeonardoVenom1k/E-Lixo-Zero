import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';

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

  users: User[] = [];
  message = '';
  error = '';

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.usersService.list().subscribe({
      next: (users) => (this.users = users),
      error: () => (this.error = 'Erro ao carregar usuários.'),
    });
  }

  toggleActive(user: User): void {
    this.clearMessages();
    this.usersService.update(Number(user.id), { ...user, active: !user.active }).subscribe({
      next: () => {
        this.message = `Usuário ${user.active ? 'desativado' : 'ativado'} com sucesso.`;
        this.load();
      },
      error: () => (this.error = 'Erro ao atualizar usuário.'),
    });
  }

  remove(user: User): void {
    if (!confirm(`Excluir o usuário "${user.fullName}"? Essa ação não pode ser desfeita.`)) {
      return;
    }
    this.clearMessages();
    this.usersService.delete(Number(user.id)).subscribe({
      next: () => {
        this.message = 'Usuário excluído com sucesso.';
        this.load();
      },
      error: () => (this.error = 'Erro ao excluir usuário. Ele pode ter coletas ou notificações vinculadas.'),
    });
  }

  typeLabel(userType?: string): string {
    return userType === 'ADMIN' ? 'Administrador' : 'Cidadão';
  }

  private clearMessages(): void {
    this.message = '';
    this.error = '';
  }
}
