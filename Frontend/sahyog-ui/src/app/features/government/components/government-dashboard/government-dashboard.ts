import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GovernmentService } from '../../services/government-service';
import { AuthService } from '../../../auth/services/auth-service';

@Component({
  selector: 'app-government-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './government-dashboard.html',
})
export class GovernmentDashboard implements OnInit {
  private govService = inject(GovernmentService);
  public auth = inject(AuthService);

  disasters = signal<any[]>([]);
  regions   = signal<any[]>([]);
  loading   = signal(true);
  showForm  = signal(false);
  saving    = signal(false);
  success   = signal('');
  error     = signal('');

  form = {
    regionId: 0,
    name: '',
    disasterType: '',
    severityLevel: '',
    startDate: '',
    endDate: ''
  };

  readonly disasterTypes = [
    'FLOOD', 'CYCLONE', 'EARTHQUAKE', 'DROUGHT', 'LANDSLIDE',
    'TSUNAMI', 'WILDFIRE', 'HEATWAVE', 'STORM', 'OTHER'
  ];
  readonly severityLevels = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];

  ngOnInit() {
    this.govService.getAllDisasters().subscribe({ next: d => { this.disasters.set(d); this.loading.set(false); }});
    this.govService.getRegions().subscribe({ next: r => this.regions.set(r) });
  }

  declare() {
    if (!this.form.regionId || !this.form.name || !this.form.disasterType || !this.form.severityLevel || !this.form.startDate) {
      this.error.set('Region, name, type, severity and start date are required.'); return;
    }
    this.saving.set(true); this.error.set('');
    const { regionId, ...body } = this.form;
    this.govService.declareDisaster(regionId, body).subscribe({
      next: d => {
        this.disasters.update(list => [d, ...list]);
        this.success.set('Disaster declared successfully.');
        this.form = { regionId: 0, name: '', disasterType: '', severityLevel: '', startDate: '', endDate: '' };
        this.showForm.set(false); this.saving.set(false);
        setTimeout(() => this.success.set(''), 3000);
      },
      error: err => { this.error.set(err?.error?.message || 'Failed.'); this.saving.set(false); }
    });
  }

  close(id: number) {
    this.govService.closeDisaster(id).subscribe({
      next: updated => {
        this.disasters.update(list => list.map(d => d.id === id ? updated : d));
        this.success.set('Disaster closed.');
        setTimeout(() => this.success.set(''), 3000);
      },
      error: err => this.error.set(err?.error?.message || 'Failed to close.')
    });
  }

  updateRisk(regionId: number, newLevel: string) {
    if (!newLevel) return;
    this.govService.updateRegionRiskLevel(regionId, newLevel).subscribe({
      next: updated => {
        this.regions.update(list => list.map(r => r.id === regionId ? updated : r));
        this.success.set(`Region "${updated.name}" risk level updated to ${newLevel}.`);
        setTimeout(() => this.success.set(''), 4000);
      },
      error: err => this.error.set(err?.error?.message || 'Failed to update risk level.')
    });
  }

  logout() {
    if (confirm('Are you sure you want to log out?')) {
      this.auth.logout();
    }
  }
}
