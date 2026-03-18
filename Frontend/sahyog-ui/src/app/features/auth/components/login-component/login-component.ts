import { Component, inject, signal } from '@angular/core';
import { AuthService } from '../../services/auth-service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-login-component',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './login-component.html',
  styleUrl: './login-component.css',
})
export class LoginComponent {
  private authService = inject(AuthService);

  username = '';
  password = '';
  loading = signal(false);
  errorMsg = signal('');

  login() {
    if (!this.username || !this.password) {
      this.errorMsg.set('Please enter your email and password.');
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.username)) {
      this.errorMsg.set('Please enter a valid email address.');
      return;
    }
    this.loading.set(true);
    this.errorMsg.set('');
    this.authService.login(this.username, this.password, {
      onError: (msg: string) => {
        this.errorMsg.set(msg);
        this.loading.set(false);
      },
      onSuccess: () => {
        this.loading.set(false);
      }
    });
  }
}
