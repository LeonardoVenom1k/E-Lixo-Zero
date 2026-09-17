import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  email = '';
  password = '';
  error = signal('');

  login(): void {
  console.log('EMAIL:', this.email);
  console.log('PASSWORD:', this.password);

  this.auth.login(this.email.trim(), this.password.trim()).subscribe({
    next: (valid) => {
      console.log('LOGIN RESULT:', valid);

      if (valid) {
        this.error.set('');
        const destination = this.auth.isAdmin()
          ? '/admin'
          : this.auth.isCollector()
            ? '/collector/pickups'
            : '/dashboard';
        this.router.navigate([destination]);
      } else {
        this.error.set('E-mail ou senha inválidos.');
      }
    },
    error: (error) => {
      console.error('REQUEST ERROR:', error);
      this.error.set('Erro ao conectar com a API.');
    },
  });
}
}