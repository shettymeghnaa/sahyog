import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';

export interface LoginCallbacks {
  onError?: (msg: string) => void;
  onSuccess?: () => void;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private apiUrl = 'http://localhost:8080/api/auth';

  token = signal<string | null>(localStorage.getItem('token'));
  role  = signal<string | null>(localStorage.getItem('role'));
  isLoggedIn = signal<boolean>(!!localStorage.getItem('token'));

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  login(username: string, password: string, callbacks?: LoginCallbacks) {
    this.http.post<{ token: string; role: string }>(`${this.apiUrl}/login`, {
      username,
      password
    })
    .subscribe({
      next: res => {
        localStorage.setItem('token', res.token);
        localStorage.setItem('role', res.role);

        this.token.set(res.token);
        this.role.set(res.role);
        this.isLoggedIn.set(true);

        callbacks?.onSuccess?.();

        if (res.role === 'ROLE_ADMIN') {
          this.router.navigate(['/admin/dashboard']);
        } else if (res.role === 'ROLE_CLAIM_OFFICER') {
          this.router.navigate(['/claim-officer/dashboard']);
        } else if (res.role === 'ROLE_GOVERNMENT') {
          this.router.navigate(['/government/dashboard']);
        } else {
          this.router.navigate(['/dashboard']);
        }
      },
      error: err => {
        const msg = err?.error?.message
          || (err.status === 401 ? 'Invalid email or password.' : 'Login failed. Please try again.');
        callbacks?.onError?.(msg);
      }
    });
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    this.token.set(null);
    this.role.set(null);
    this.isLoggedIn.set(false);
    this.router.navigate(['/login']);
  }

  getRole(): string | null { return this.role(); }
  isAdmin(): boolean { return this.role() === 'ROLE_ADMIN'; }
  isMember(): boolean { return this.role() === 'ROLE_MEMBER'; }
  isClaimOfficer(): boolean { return this.role() === 'ROLE_CLAIM_OFFICER'; }
  isGovernment(): boolean { return this.role() === 'ROLE_GOVERNMENT'; }
}
