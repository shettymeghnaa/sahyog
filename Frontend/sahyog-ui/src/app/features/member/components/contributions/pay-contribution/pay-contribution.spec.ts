import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PayContribution } from './pay-contribution';
import { HttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { ContributionService } from '../../../services/contribution-service';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { FormsModule } from '@angular/forms';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

describe('PayContribution', () => {
  let component: PayContribution;
  let fixture: ComponentFixture<PayContribution>;
  let httpTestingController: HttpTestingController;
  let contributionService: any;

  beforeEach(async () => {
    contributionService = {
      payContribution: vi.fn().mockReturnValue(of({}))
    };

    await TestBed.configureTestingModule({
      imports: [PayContribution, FormsModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: ContributionService, useValue: contributionService }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PayContribution);
    component = fixture.componentInstance;
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should create and load member info on init', () => {
    const mockMember = { id: 1, region: { id: 101, riskLevel: 'MEDIUM' } };
    const mockPolicy = { monthlyContribution: 100 };

    fixture.detectChanges(); // triggers ngOnInit

    const memberReq = httpTestingController.expectOne('http://localhost:8080/api/members/me');
    memberReq.flush(mockMember);

    const policyReq = httpTestingController.expectOne('http://localhost:8080/api/policies/region/101');
    policyReq.flush(mockPolicy);

    expect(component).toBeTruthy();
    expect(component.memberInfo()).toEqual(mockMember);
    expect(component.calculatedAmount()).toBe(125); // 100 * 1.25 for MEDIUM risk
  });

  it('should initiate payment and show confirmation', () => {
    component.month = new Date().toISOString().substring(0, 7);
    component.initiatePayment();
    expect(component.showConfirmation()).toBe(true);
  });

  it('should proceed with payment on confirmAndPay', () => {
    const currentMonth = new Date().toISOString().substring(0, 7);
    component.month = currentMonth;
    
    component.confirmAndPay();

    expect(contributionService.payContribution).toHaveBeenCalledWith(`${currentMonth}-01`);
  });
});
