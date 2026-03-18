import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminOverview } from './admin-overview';
import { AdminService } from '../../services/admin-service';
import { provideRouter } from '@angular/router';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { of } from 'rxjs';

describe('AdminOverview', () => {
  let component: AdminOverview;
  let fixture: ComponentFixture<AdminOverview>;
  let adminService: any;

  beforeEach(async () => {
    adminService = {
      getRegions: vi.fn().mockReturnValue(of([])),
      getAllMembers: vi.fn().mockReturnValue(of([])),
      getAllDisasters: vi.fn().mockReturnValue(of([])),
      getAllClaims: vi.fn().mockReturnValue(of([])),
      getGlobalPoolSummary: vi.fn().mockReturnValue(of({})),
    };

    await TestBed.configureTestingModule({
      imports: [AdminOverview],
      providers: [
        { provide: AdminService, useValue: adminService },
        provideRouter([])
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminOverview);
    component = fixture.componentInstance;
  });

  it('should create and load all stats on init', () => {
    const mockRegions = [1, 2];
    const mockMembers = [1, 2, 3];
    const mockDisasters = [{ status: 'ACTIVE' }, { status: 'CLOSED' }];
    const mockClaims = [{ status: 'SUBMITTED' }, { status: 'APPROVED' }, { status: 'SUBMITTED' }];

    adminService.getRegions.mockReturnValue(of(mockRegions));
    adminService.getAllMembers.mockReturnValue(of(mockMembers));
    adminService.getAllDisasters.mockReturnValue(of(mockDisasters));
    adminService.getAllClaims.mockReturnValue(of(mockClaims));

    fixture.detectChanges(); // triggers ngOnInit

    expect(component).toBeTruthy();
    expect(component.regions().length).toBe(2);
    expect(component.members().length).toBe(3);
    expect(component.activeDisasters).toBe(1);
    expect(component.pendingClaims).toBe(2);
    expect(component.paidClaims).toBe(1);
  });
});
