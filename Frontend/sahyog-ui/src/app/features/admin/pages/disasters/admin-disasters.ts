import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../services/admin-service';

@Component({
  selector: 'app-admin-disasters',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-disasters.html',
})
export class AdminDisasters implements OnInit {
  private admin = inject(AdminService);

  disasters = signal<any[]>([]);
  regions   = signal<any[]>([]);
  loading   = signal(true);
  success   = signal('');
  error     = signal('');

  // Form State
  showForm  = signal(false);
  saving    = signal(false);
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
    this.admin.getAllDisasters().subscribe({ next: d => { this.disasters.set(d); this.loading.set(false); }});
    this.admin.getRegions().subscribe({ next: r => this.regions.set(r) });
  }

  declare() {
    if (!this.form.regionId || !this.form.name || !this.form.disasterType || !this.form.severityLevel || !this.form.startDate) {
      this.error.set('Region, name, type, severity and start date are required.'); return;
    }
    this.saving.set(true); this.error.set('');
    
    const { regionId, ...body } = this.form;
    
    this.admin.declareDisaster(regionId, body).subscribe({
      next: d => {
        this.disasters.update(list => [d, ...list]);
        this.success.set('Disaster declared successfully.');
        this.form = { regionId: 0, name: '', disasterType: '', severityLevel: '', startDate: '', endDate: '' };
        this.showForm.set(false); 
        this.saving.set(false);
        setTimeout(() => this.success.set(''), 3000);
      },
      error: err => { this.error.set(err?.error?.message || 'Failed to declare disaster.'); this.saving.set(false); }
    });
  }

  close(id: number) {
    if (!confirm('Are you sure you want to close this disaster event? claims can no longer be filed.')) return;
    
    this.admin.closeDisaster(id).subscribe({
      next: updated => {
        this.disasters.update(list => list.map(d => d.id === id ? updated : d));
        this.success.set('Disaster closed.');
        setTimeout(() => this.success.set(''), 3000);
      },
      error: err => this.error.set(err?.error?.message || 'Failed to close disaster.')
    });
  }
}
