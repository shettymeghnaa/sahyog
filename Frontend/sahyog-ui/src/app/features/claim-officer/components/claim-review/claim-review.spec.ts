import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ClaimReviewComponent } from './claim-review';
import { ClaimOfficerService } from '../../services/claim-officer-service';
import { AdminService } from '../../../admin/services/admin-service';
import { ActivatedRoute, Router } from '@angular/router';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { of } from 'rxjs';
import { FormsModule } from '@angular/forms';

describe('ClaimReviewComponent', () => {
  let component: ClaimReviewComponent;
  let fixture: ComponentFixture<ClaimReviewComponent>;
  let officerService: any;
  let adminService: any;
  let router: any;
  let activatedRoute: any;

  beforeEach(async () => {
    officerService = {
      getClaimById: vi.fn(),
      reviewClaim: vi.fn().mockReturnValue(of({})),
      approveClaim: vi.fn().mockReturnValue(of({})),
      rejectClaim: vi.fn().mockReturnValue(of({})),
    };
    adminService = {};
    router = {
      navigate: vi.fn(),
    };
    activatedRoute = {
      snapshot: {
        paramMap: {
          get: vi.fn().mockReturnValue('123')
        }
      }
    };

    await TestBed.configureTestingModule({
      imports: [ClaimReviewComponent, FormsModule],
      providers: [
        { provide: ClaimOfficerService, useValue: officerService },
        { provide: AdminService, useValue: adminService },
        { provide: Router, useValue: router },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClaimReviewComponent);
    component = fixture.componentInstance;
  });

  it('should load claim and auto-review if SUBMITTED', () => {
    const mockClaim = { id: 123, status: 'SUBMITTED' };
    officerService.getClaimById.mockReturnValue(of(mockClaim));

    fixture.detectChanges(); // ngOnInit

    expect(officerService.getClaimById).toHaveBeenCalledWith(123);
    expect(officerService.reviewClaim).toHaveBeenCalledWith(123);
    expect(component.claim().status).toBe('UNDER_REVIEW');
  });

  it('should submit approval decision', () => {
    component.claim.set({ id: 123, status: 'UNDER_REVIEW' });
    component.notes = 'Verified';
    
    component.submitDecision('APPROVE');

    expect(officerService.approveClaim).toHaveBeenCalledWith(123, 'Verified');
    expect(router.navigate).toHaveBeenCalledWith(['/claim-officer/dashboard']);
  });
});
