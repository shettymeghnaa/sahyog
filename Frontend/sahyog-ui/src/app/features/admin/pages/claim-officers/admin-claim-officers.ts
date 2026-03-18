import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../services/admin-service';

@Component({
  selector: 'app-admin-claim-officers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-claim-officers.html',
})
export class AdminClaimOfficers implements OnInit {
  private admin = inject(AdminService);

  saving   = signal(false);
  success  = signal('');
  error    = signal('');
  officers = signal<any[]>([]);
  regions  = signal<any[]>([]);

  form = {
    fullName: '',
    email: '',
    password: '',
    regionId: 0,
    status: 'ACTIVE'
  };
  showPassword = false;

  deactivating = signal<number | null>(null);
  reassigning  = signal<number | null>(null);
  selectedNewRegionId = signal<number>(0);

  ngOnInit() {
    this.admin.getRegions().subscribe(r => this.regions.set(r));
    this.admin.getClaimOfficers().subscribe({ next: o => this.officers.set(o), error: () => {} });
  }

  register() {
    this.error.set(''); this.success.set('');

    if (!this.form.fullName.trim()) { this.error.set('Full name is required.'); return; }
    if (!this.form.email || !this.form.email.includes('@')) { this.error.set('Enter a valid email.'); return; }
    if (!this.form.password || this.form.password.length < 6) { this.error.set('Password must be at least 6 characters.'); return; }
    if (!this.form.regionId) { this.error.set('Please select a region.'); return; }

    this.saving.set(true);
    const { regionId, ...body } = this.form;
    this.admin.createClaimOfficer(regionId, body).subscribe({
      next: officer => {
        this.success.set(`Claim Officer "${officer.fullName}" created successfully.`);
        this.officers.update(list => [officer, ...list]);
        this.form = { fullName: '', email: '', password: '', regionId: 0, status: 'ACTIVE' };
        this.saving.set(false);
        setTimeout(() => this.success.set(''), 4000);
      },
      error: err => {
        this.error.set(err?.error?.message || 'Failed to create claim officer.');
        this.saving.set(false);
      }
    });
  }

  deactivate(id: number) {
    if (!confirm('Are you sure you want to deactivate this officer? They will no longer be able to log in.')) return;
    
    this.deactivating.set(id);
    this.admin.deactivateClaimOfficer(id).subscribe({
      next: () => {
        this.officers.update(list => list.map(o => o.id === id ? { ...o, status: 'INACTIVE' } : o));
        this.deactivating.set(null);
        this.success.set('Officer deactivated successfully.');
        setTimeout(() => this.success.set(''), 4000);
      },
      error: (err) => {
        this.error.set(err?.error?.message || 'Failed to deactivate officer.');
        this.deactivating.set(null);
      }
    });
  }

  startReassign(id: number) {
    this.reassigning.set(id);
    const officer = this.officers().find(o => o.id === id);
    this.selectedNewRegionId.set(officer?.region?.id || 0);
  }

  cancelReassign() {
    this.reassigning.set(null);
  }

  reassignRegion() {
    const id = this.reassigning();
    const regionId = this.selectedNewRegionId();
    if (!id || !regionId) return;

    this.saving.set(true);
    this.admin.reassignOfficerRegion(id, regionId).subscribe({
      next: () => {
        const newRegion = this.regions().find(r => r.id === regionId);
        this.officers.update(list => list.map(o => o.id === id ? { ...o, region: newRegion } : o));
        this.reassigning.set(null);
        this.saving.set(false);
        this.success.set('Officer reassigned successfully.');
        setTimeout(() => this.success.set(''), 4000);
      },
      error: (err) => {
        this.error.set(err?.error?.message || 'Failed to reassign officer.');
        this.saving.set(false);
      }
    });
  }
}
