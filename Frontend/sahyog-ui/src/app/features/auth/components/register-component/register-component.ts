import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-register-component',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './register-component.html',
  styleUrl: './register-component.css',
})
export class RegisterComponent {

  regions: any[] = [];
  regionId: number = 0;
  fullName = '';
  email = '';
  phoneNumber = '';
  password = '';
  confirmPassword = '';
  acceptedTerms = false;

  isSuccessView = signal(false);

  loading = signal(false);
  errorMsg = signal('');
  successMsg = signal('');

  private api = 'http://localhost:8080/api/members';

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit() {
    this.http.get<any[]>('http://localhost:8080/api/regions').subscribe({
      next: res => this.regions = res,
      error: () => this.errorMsg.set('Could not load regions. Is the server running?')
    });
  }

  register() {
    this.errorMsg.set('');
    this.successMsg.set('');

    if (!this.fullName || !this.email || !this.password) {
      this.errorMsg.set('Please fill in all fields.');
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.email)) {
      this.errorMsg.set('Please enter a valid email address.');
      return;
    }

    if (!this.phoneNumber || this.phoneNumber.length < 10) {
      this.errorMsg.set('Please enter a valid phone number.');
      return;
    }

    if (this.password.length < 6) {
      this.errorMsg.set('Password must be at least 6 characters long.');
      return;
    }
    
    if (this.password !== this.confirmPassword) {
      this.errorMsg.set('Passwords do not match.');
      return;
    }
    if (!this.regionId || this.regionId === 0) {
      this.errorMsg.set('Please select your community region.');
      return;
    }
    if (!this.acceptedTerms) {
      this.errorMsg.set('You must accept the terms and conditions.');
      return;
    }

    this.loading.set(true);

    const body = { 
      fullName: this.fullName, 
      email: this.email, 
      phoneNumber: this.phoneNumber,
      password: this.password,
      acceptedTerms: this.acceptedTerms 
    };

    this.http.post(`${this.api}/region/${this.regionId}`, body).subscribe({
      next: () => {
        this.loading.set(false);
        this.isSuccessView.set(true);
      },
      error: err => {
        this.loading.set(false);
        this.errorMsg.set(err?.error?.message || 'Registration failed. Please try again.');
      }
    });
  }
}
