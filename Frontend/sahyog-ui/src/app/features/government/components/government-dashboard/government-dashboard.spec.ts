import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GovernmentDashboard } from './government-dashboard';
import { GovernmentService } from '../../services/government-service';
import { AuthService } from '../../../auth/services/auth-service';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { of } from 'rxjs';
import { FormsModule } from '@angular/forms';

describe('GovernmentDashboard', () => {
  let component: GovernmentDashboard;
  let fixture: ComponentFixture<GovernmentDashboard>;
  let govService: any;
  let authService: any;

  beforeEach(async () => {
    govService = {
      getAllDisasters: vi.fn().mockReturnValue(of([])),
      getRegions: vi.fn().mockReturnValue(of([])),
      declareDisaster: vi.fn(),
      closeDisaster: vi.fn(),
      updateRegionRiskLevel: vi.fn(),
    };
    authService = {
      logout: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [GovernmentDashboard, FormsModule],
      providers: [
        { provide: GovernmentService, useValue: govService },
        { provide: AuthService, useValue: authService },
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GovernmentDashboard);
    component = fixture.componentInstance;
  });

  it('should load data on init', () => {
    const mockDisasters = [{ id: 1, name: 'Storm' }];
    const mockRegions = [{ id: 101, name: 'West' }];
    govService.getAllDisasters.mockReturnValue(of(mockDisasters));
    govService.getRegions.mockReturnValue(of(mockRegions));

    fixture.detectChanges();

    expect(component.disasters()).toEqual(mockDisasters);
    expect(component.regions()).toEqual(mockRegions);
  });

  it('should declare a disaster', () => {
    component.form = {
      regionId: 101,
      name: 'Test Disaster',
      disasterType: 'FLOOD',
      severityLevel: 'HIGH',
      startDate: '2025-01-01',
      endDate: ''
    };
    govService.declareDisaster.mockReturnValue(of({ id: 5, name: 'Test Disaster' }));

    component.declare();

    expect(govService.declareDisaster).toHaveBeenCalled();
    expect(component.success()).toContain('success');
  });

  it('should update region risk', () => {
    govService.updateRegionRiskLevel.mockReturnValue(of({ id: 101, name: 'West' }));
    component.updateRisk(101, 'CRITICAL');
    expect(govService.updateRegionRiskLevel).toHaveBeenCalledWith(101, 'CRITICAL');
  });
});
