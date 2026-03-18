import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../services/admin-service';

@Component({
  selector: 'app-admin-members',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-members.html',
})
export class AdminMembers implements OnInit {
  private admin = inject(AdminService);

  members = signal<any[]>([]);
  loading = signal(true);
  success = signal('');
  error   = signal('');

  ngOnInit() {
    this.admin.getAllMembers().subscribe({
      next: (m: any[]) => { 
        m.sort((a, b) => {
          if (a.status === 'PENDING' && b.status !== 'PENDING') return -1;
          if (b.status === 'PENDING' && a.status !== 'PENDING') return 1;
          return 0;
        });
        this.members.set(m); 
        this.loading.set(false); 
      },
      error: () => { this.error.set('Failed to load members.'); this.loading.set(false); }
    });
  }

  suspend(id: number, name: string) {
    this.admin.suspendMember(id).subscribe({
      next: updated => {
        this.members.update(list => list.map(m => m.id === id ? updated : m));
        this.success.set(`${name} suspended.`);
        setTimeout(() => this.success.set(''), 3000);
      },
      error: err => this.error.set(err?.error?.message || 'Failed to suspend.')
    });
  }

  activate(id: number, name: string) {
    this.admin.activateMember(id).subscribe({
      next: updated => {
        this.members.update(list => list.map(m => m.id === id ? updated : m));
        this.success.set(`${name} activated.`);
        setTimeout(() => this.success.set(''), 3000);
      },
      error: err => this.error.set(err?.error?.message || 'Failed to activate.')
    });
  }
}
