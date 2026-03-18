import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../services/admin-service';

@Component({
  selector: 'app-admin-claims',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-claims.html',
})
export class AdminClaims implements OnInit {
  private admin = inject(AdminService);

  claims  = signal<any[]>([]);
  loading = signal(true);
  error   = signal('');
  success = signal('');
  filter  = signal<string>('ALL');

  ngOnInit() {
    this.refresh();
  }

  refresh() {
    this.loading.set(true);
    this.admin.getAllClaims().subscribe({
      next: c => { this.claims.set(c); this.loading.set(false); },
      error: () => { this.error.set('Failed to load claims.'); this.loading.set(false); }
    });
  }

  pay(id: number) {
    if (!confirm('Are you sure you want to release the payout for this claim?')) return;
    
    this.admin.payClaim(id).subscribe({
      next: () => {
        this.success.set('Payout released successfully!');
        this.refresh();
        setTimeout(() => this.success.set(''), 3000);
      },
      error: (err) => {
        this.error.set(err?.error?.message || 'Failed to release payout.');
        setTimeout(() => this.error.set(''), 4000);
      }
    });
  }

  get filtered() {
    const f = this.filter();
    return f === 'ALL' ? this.claims() : this.claims().filter(c => c.status === f);
  }

  setFilter(v: string) { this.filter.set(v); }
}
