import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AdminService } from '../../services/admin-service';

@Component({
  selector: 'app-admin-overview',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './admin-overview.html',
})
export class AdminOverview implements OnInit {
  private admin = inject(AdminService);

  regions   = signal<any[]>([]);
  members   = signal<any[]>([]);
  disasters = signal<any[]>([]);
  claims    = signal<any[]>([]);
  loading   = signal(true);
  poolSummary = signal<any>(null);

  ngOnInit() {
    // Load all stats in parallel
    this.admin.getRegions().subscribe(r => this.regions.set(r));
    this.admin.getAllMembers().subscribe(m => this.members.set(m));
    this.admin.getAllDisasters().subscribe(d => this.disasters.set(d));
    this.admin.getAllClaims().subscribe(c => { this.claims.set(c); this.loading.set(false); });
    this.admin.getGlobalPoolSummary().subscribe({
      next: (s) => this.poolSummary.set(s),
      error: () => {} // silently fail if no pool exists yet
    });
  }

  get activeDisasters() { return this.disasters().filter(d => d.status === 'ACTIVE').length; }
  get pendingClaims()   { return this.claims().filter(c => c.status === 'SUBMITTED').length; }
  get paidClaims()      { return this.claims().filter(c => c.status === 'APPROVED').length; }
}
