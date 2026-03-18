import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminRegions } from './admin-regions';
import { AdminService } from '../../services/admin-service';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { of } from 'rxjs';
import { FormsModule } from '@angular/forms';

describe('AdminRegions', () => {
  let component: AdminRegions;
  let fixture: ComponentFixture<AdminRegions>;
  let adminService: any;

  beforeEach(async () => {
    adminService = {
      getRegions: vi.fn().mockReturnValue(of([])),
      createRegion: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [AdminRegions, FormsModule],
      providers: [
        { provide: AdminService, useValue: adminService }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminRegions);
    component = fixture.componentInstance;
  });

  it('should load regions on init', () => {
    const mockRegions = [{ id: 1, name: 'North' }];
    adminService.getRegions.mockReturnValue(of(mockRegions));
    
    fixture.detectChanges();

    expect(component.regions()).toEqual(mockRegions);
    expect(component.loading()).toBe(false);
  });

  it('should validate form and call createRegion', () => {
    component.form = { name: 'New', state: 'S', country: 'C', status: 'ACTIVE' };
    adminService.createRegion.mockReturnValue(of({ id: 2, name: 'New' }));

    component.submit();

    expect(adminService.createRegion).toHaveBeenCalled();
    expect(component.success()).toContain('created');
  });
});
