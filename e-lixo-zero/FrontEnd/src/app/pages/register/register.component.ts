import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

import { AuthService } from '../../services/auth';
import { User, UserCompat } from '../../models/user.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, RouterLink, CommonModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
})
export class RegisterComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  name = '';
  email = '';
  password = '';
  confirmPassword = '';

  street = '';
  number = '';
  neighborhood = '';
  city = 'Santa Rita do Sapucaí';

  error = signal('');

  register(): void {
    if (this.password !== this.confirmPassword) {
      this.error.set('As senhas não coincidem.');
      return;
    }

    this.auth
      .register({
        fullName: this.name,
        email: this.email,
        password: this.password,
        street: this.street,
        number: this.number,
        neighborhood: this.neighborhood,
        city: this.city,
        state: 'MG',
        phone: '',
        userType: 'CITIZEN',
        cpf: '' // CPF optional for now
      })
      .subscribe({
        next: (user) => {
          // Convert to compatible format
          const userCompat: UserCompat = {
            id: String(user.id),
            name: user.fullName,
            email: user.email,
            password: user.password,
            street: user.street,
            number: user.number,
            neighborhood: user.neighborhood,
            city: user.city
          };
          localStorage.setItem('loggedInUser', JSON.stringify(userCompat));
          this.router.navigate(['/dashboard']);
        },
        error: () => {
          this.error.set('Erro ao cadastrar usuário.');
        },
      });
  }
}