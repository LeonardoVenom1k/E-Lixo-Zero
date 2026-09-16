import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
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
  error = '';

  login(): void {
  console.log('EMAIL:', this.email);
  console.log('PASSWORD:', this.password);

  this.auth.login(this.email.trim(), this.password.trim()).subscribe({
    next: (valid) => {
      console.log('LOGIN RESULT:', valid);

      if (valid) {
        this.error = '';
        this.router.navigate([this.auth.isAdmin() ? '/admin' : '/dashboard']);
      } else {
        this.error = 'E-mail ou senha inválidos.';
      }
    },
    error: (error) => {
      console.error('REQUEST ERROR:', error);
      this.error = 'Erro ao conectar com a API.';
    },
  });
}
}