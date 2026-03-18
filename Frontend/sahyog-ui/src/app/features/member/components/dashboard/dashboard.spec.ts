import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Dashboard } from './dashboard';
import { HttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { MemberService } from '../../services/member-service';
import { ClaimService } from '../../services/claim-service';
import { ContributionService } from '../../services/contribution-service';
import { provideRouter } from '@angular/router';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { of } from 'rxjs';

describe('Dashboard', () => {
  let component: Dashboard;
  let fixture: ComponentFixture<Dashboard>;
  let httpTestingController: HttpTestingController;
  let claimService: any;
  let contributionService: any;

  beforeEach(async () => {
    claimService = {
      getMyClaims: vi.fn().mockReturnValue(of([]))
    };
    contributionService = {
      getMyContributions: vi.fn().mockReturnValue(of([]))
    };

    await TestBed.configureTestingModule({
      imports: [Dashboard],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: ClaimService, useValue: claimService },
        { provide: ContributionService, useValue: contributionService },
        MemberService
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Dashboard);
    component = fixture.componentInstance;
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should create and load data on init', () => {
    const mockMember = { id: 1, fullName: 'John Doe', region: { id: 101 } };
    const mockDisasters = [{ id: 1, name: 'Flood' }];

    fixture.detectChanges(); // triggers ngOnInit

    // Expect member call
    const memberReq = httpTestingController.expectOne('http://localhost:8080/api/members/me');
    memberReq.flush(mockMember);

    // Expect disasters call
    const disasterReq = httpTestingController.expectOne('http://localhost:8080/api/disasters/active/region/101');
    disasterReq.flush(mockDisasters);

    expect(component).toBeTruthy();
    expect(component.member()).toEqual(mockMember);
    expect(component.activeDisasters()).toEqual(mockDisasters);
    expect(claimService.getMyClaims).toHaveBeenCalled();
    expect(contributionService.getMyContributions).toHaveBeenCalled();
  });

  it('should change view correctly', () => {
    component.setView('claims');
    expect(component.activeView()).toBe('claims');
  });

  it('should filter disasters based on claims', () => {
    component.allDisasters = [{ id: 1, name: 'D1' }, { id: 2, name: 'D2' }];
    component.claims.set([{ disasterId: 1 }]);
    
    component.filterDisasters();
    
    expect(component.activeDisasters().length).toBe(1);
    expect(component.activeDisasters()[0].id).toBe(2);
  });
});
