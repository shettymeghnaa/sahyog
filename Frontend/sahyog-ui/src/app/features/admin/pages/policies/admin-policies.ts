import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../services/admin-service';

@Component({
  selector: 'app-admin-policies',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-policies.html',
})
export class AdminPolicies {
  private admin = inject(AdminService);

  regions  = signal<any[]>([]);
  policy   = signal<any>(null);
  loading  = signal(false);
  saving   = signal(false);
  success  = signal('');
  error    = signal('');

  selectedRegionId = 0;
  form = { monthlyContribution: 0, maxPayoutPerClaim: 0, waitingPeriodDays: 0, reservePercentage: 20, minContributionsRequired: 0 };
  isNew = false;

  ngOnInit() {
    this.admin.getRegions().subscribe({ next: r => this.regions.set(r) });
  }

  loadPolicy() {
    if (!this.selectedRegionId) return;
    this.loading.set(true); this.policy.set(null); this.error.set(''); this.success.set('');

    this.admin.getPolicyByRegion(this.selectedRegionId).subscribe({
      next: p => {
        this.policy.set(p);
        this.form = {
          monthlyContribution: p.monthlyContribution,
          maxPayoutPerClaim: p.maxPayoutPerClaim,
          waitingPeriodDays: p.waitingPeriodDays,
          reservePercentage: p.reservePercentage,
          minContributionsRequired: p.minContributionsRequired || 0
        };
        this.isNew = false;
        this.loading.set(false);
      },
      error: () => {
        // 404 means no policy yet — show create form
        this.isNew = true;
        this.form = { monthlyContribution: 500, maxPayoutPerClaim: 50000, waitingPeriodDays: 30, reservePercentage: 20, minContributionsRequired: 0 };
        this.loading.set(false);
      }
    });
  }

  save() {
    this.error.set('');
    
    // Front-end validation rules
    if (this.form.monthlyContribution <= 0) {
      this.error.set('Base Monthly Contribution must be greater than 0.'); return;
    }
    if (this.form.maxPayoutPerClaim <= 0) {
      this.error.set('Max Payout must be greater than 0.'); return;
    }
    if (this.form.reservePercentage < 0 || this.form.reservePercentage > 100) {
      this.error.set('Reserve Percentage must be between 0 and 100.'); return;
    }

    this.saving.set(true);
    const req = this.isNew
      ? this.admin.createPolicy(this.selectedRegionId, this.form)
      : this.admin.updatePolicy(this.policy()?.id, this.form);

    req.subscribe({
      next: p => {
        this.policy.set(p); this.isNew = false;
        this.success.set('Policy saved successfully.');
        this.saving.set(false);
        setTimeout(() => this.success.set(''), 3000);
      },
      error: err => { this.error.set(err?.error?.message || 'Failed to save.'); this.saving.set(false); }
    });
  }
}
