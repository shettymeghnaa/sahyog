import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SubmitClaim } from './submit-claim';
import { HttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { ClaimService } from '../../../services/claim-service';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { FormsModule } from '@angular/forms';
import { of } from 'rxjs';

describe('SubmitClaim', () => {
  let component: SubmitClaim;
  let fixture: ComponentFixture<SubmitClaim>;
  let httpTestingController: HttpTestingController;
  let claimService: any;

  beforeEach(async () => {
    claimService = {
      submitClaim: vi.fn().mockReturnValue(of({}))
    };

    await TestBed.configureTestingModule({
      imports: [SubmitClaim, FormsModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ClaimService, useValue: claimService }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SubmitClaim);
    component = fixture.componentInstance;
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should create and load profile on init', () => {
    const mockMember = { id: 1, region: { id: 101 } };
    const mockPolicy = { maxPayoutPerClaim: 5000 };
    const mockDisasters = [{ id: 1, name: 'Flood' }];

    fixture.detectChanges(); // triggers ngOnInit

    const memberReq = httpTestingController.expectOne('http://localhost:8080/api/members/me');
    memberReq.flush(mockMember);

    const policyReq = httpTestingController.expectOne('http://localhost:8080/api/policies/region/101');
    policyReq.flush(mockPolicy);

    const disasterReq = httpTestingController.expectOne('http://localhost:8080/api/disasters/active/region/101');
    disasterReq.flush(mockDisasters);

    expect(component).toBeTruthy();
    expect(component.policyMaxPayout).toBe(5000);
    expect(component.disasters()).toEqual(mockDisasters);
  });

  it('should validate form before submission', () => {
    component.disasterId = 0;
    component.submit();
    expect(component.errorMsg).toBe('Please select a disaster and enter a valid amount.');

    component.disasterId = 1;
    component.requestedAmount = 100;
    component.description = 'short';
    component.submit();
    expect(component.errorMsg).toBe('Please provide a clear description of the damage (at least 10 characters).');
  });

  it('should call claimService.submitClaim on valid input', () => {
    component.disasterId = 1;
    component.requestedAmount = 1000;
    component.description = 'Valid description here';
    component.documentUrl = 'http://docs.com';
    component.policyMaxPayout = 5000;

    component.submit();

    expect(claimService.submitClaim).toHaveBeenCalledWith({
      disasterId: 1,
      requestedAmount: 1000,
      description: 'Valid description here',
      documentUrl: 'http://docs.com'
    });
    expect(component.submitted).toBe(true);
  });
});
