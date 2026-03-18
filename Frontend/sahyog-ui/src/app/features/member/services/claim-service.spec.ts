import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { ClaimService } from './claim-service';
import { describe, it, expect, beforeEach } from 'vitest';

describe('ClaimService', () => {
  let service: ClaimService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ClaimService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(ClaimService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should submit a claim', () => {
    const mockClaim = { disasterId: 1, amount: 1000 };
    const mockResponse = { id: 1, ...mockClaim };

    service.submitClaim(mockClaim).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpTestingController.expectOne('http://localhost:8080/api/claims/submit');
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should get personal claims', () => {
    const mockClaims = [{ id: 1, amount: 1000 }];

    service.getMyClaims().subscribe(response => {
      expect(response).toEqual(mockClaims);
    });

    const req = httpTestingController.expectOne('http://localhost:8080/api/claims/my');
    expect(req.request.method).toBe('GET');
    req.flush(mockClaims);
  });
});
