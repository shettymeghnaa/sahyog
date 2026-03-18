import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminPolicies } from './admin-policies';
import { AdminService } from '../../services/admin-service';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { of, throwError } from 'rxjs';
import { FormsModule } from '@angular/forms';

describe('AdminPolicies', () => {
  let component: AdminPolicies;
  let fixture: ComponentFixture<AdminPolicies>;
  let adminService: any;

  beforeEach(async () => {
    adminService = {
      getRegions: vi.fn().mockReturnValue(of([])),
      getPolicyByRegion: vi.fn(),
      createPolicy: vi.fn(),
      updatePolicy: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [AdminPolicies, FormsModule],
      providers: [
        { provide: AdminService, useValue: adminService }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminPolicies);
    component = fixture.componentInstance;
  });

  it('should load regions on init', () => {
    const mockRegions = [{ id: 1, name: 'R1' }];
    adminService.getRegions.mockReturnValue(of(mockRegions));
    fixture.detectChanges();
    expect(component.regions()).toEqual(mockRegions);
  });

  it('should show create form if policy not found', () => {
    component.selectedRegionId = 1;
    adminService.getPolicyByRegion.mockReturnValue(throwError(() => ({ status: 404 })));

    component.loadPolicy();

    expect(component.isNew).toBe(true);
    expect(component.form.monthlyContribution).toBe(500);
  });

  it('should load existing policy', () => {
    const mockPolicy = { id: 10, monthlyContribution: 200, maxPayoutPerClaim: 1000, waitingPeriodDays: 30, reservePercentage: 15 };
    component.selectedRegionId = 1;
    adminService.getPolicyByRegion.mockReturnValue(of(mockPolicy));

    component.loadPolicy();

    expect(component.isNew).toBe(false);
    expect(component.form.monthlyContribution).toBe(200);
  });
});
