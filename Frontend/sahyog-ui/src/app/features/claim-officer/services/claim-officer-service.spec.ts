import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { ClaimOfficerService } from './claim-officer-service';
import { describe, it, expect, beforeEach } from 'vitest';

describe('ClaimOfficerService', () => {
  let service: ClaimOfficerService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ClaimOfficerService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(ClaimOfficerService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch pending claims', () => {
    const mockClaims = [{ id: 1, status: 'SUBMITTED' }];
    service.getPendingClaims().subscribe(res => expect(res).toEqual(mockClaims));

    const req = httpTestingController.expectOne('http://localhost:8080/api/claims/pending');
    expect(req.request.method).toBe('GET');
    req.flush(mockClaims);
  });

  it('should approve a claim', () => {
    const notes = 'Verified';
    service.approveClaim(1, notes).subscribe();

    const req = httpTestingController.expectOne('http://localhost:8080/api/claims/approve/1');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ verificationNotes: notes });
    req.flush({});
  });
});
