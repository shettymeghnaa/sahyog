import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../services/admin-service';

@Component({
  selector: 'app-admin-regions',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-regions.html',
})
export class AdminRegions {
  private admin = inject(AdminService);

  regions  = signal<any[]>([]);
  loading  = signal(true);
  showForm = signal(false);
  saving   = signal(false);
  success  = signal('');
  error    = signal('');

  form = { name: '', state: '', country: '', status: 'ACTIVE' };

  ngOnInit() {
    this.load();
  }

  load() {
    this.admin.getRegions().subscribe({ next: r => { this.regions.set(r); this.loading.set(false); }});
  }

  submit() {
    if (!this.form.name || !this.form.state || !this.form.country) {
      this.error.set('All fields are required.'); return;
    }
    this.saving.set(true); this.error.set('');
    this.admin.createRegion(this.form).subscribe({
      next: r => {
        this.regions.update(list => [...list, r]);
        this.success.set(`Region "${r.name}" created!`);
        this.form = { name: '', state: '', country: '', status: 'ACTIVE' };
        this.showForm.set(false);
        this.saving.set(false);
        setTimeout(() => this.success.set(''), 3000);
      },
      error: err => { this.error.set(err?.error?.message || 'Failed to create region.'); this.saving.set(false); }
    });
  }
}
