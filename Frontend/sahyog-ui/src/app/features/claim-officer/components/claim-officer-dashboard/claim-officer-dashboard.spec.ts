import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ClaimOfficerDashboard } from './claim-officer-dashboard';
import { ClaimOfficerService } from '../../services/claim-officer-service';
import { AuthService } from '../../../auth/services/auth-service';
import { provideRouter, Router } from '@angular/router';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { of } from 'rxjs';

describe('ClaimOfficerDashboard', () => {
  let component: ClaimOfficerDashboard;
  let fixture: ComponentFixture<ClaimOfficerDashboard>;
  let claimOfficerService: any;
  let authService: any;
  let router: Router;

  beforeEach(async () => {
    claimOfficerService = {
      getPendingClaims: vi.fn().mockReturnValue(of([])),
    };
    authService = {
      logout: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [ClaimOfficerDashboard],
      providers: [
        { provide: ClaimOfficerService, useValue: claimOfficerService },
        { provide: AuthService, useValue: authService },
        provideRouter([])
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClaimOfficerDashboard);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
  });

  it('should load pending claims on init', () => {
    const mockClaims = [{ id: 1, amount: 1000 }];
    claimOfficerService.getPendingClaims.mockReturnValue(of(mockClaims));

    fixture.detectChanges();

    expect(component.pendingClaims()).toEqual(mockClaims);
    expect(component.loading()).toBe(false);
  });

  it('should navigate to review page', () => {
    const navigateSpy = vi.spyOn(router, 'navigate');
    component.review({ id: 123 });
    expect(navigateSpy).toHaveBeenCalledWith(['/claim-officer/review', 123]);
  });
});
