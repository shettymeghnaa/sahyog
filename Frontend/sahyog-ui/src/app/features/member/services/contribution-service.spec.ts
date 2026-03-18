import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { ContributionService } from './contribution-service';
import { describe, it, expect, beforeEach } from 'vitest';

describe('ContributionService', () => {
  let service: ContributionService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ContributionService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(ContributionService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get personal contributions', () => {
    const mockContributions = [{ id: 1, amount: 100 }];

    service.getMyContributions().subscribe(response => {
      expect(response).toEqual(mockContributions);
    });

    const req = httpTestingController.expectOne('http://localhost:8080/api/contributions/my');
    expect(req.request.method).toBe('GET');
    req.flush(mockContributions);
  });

  it('should pay a contribution', () => {
    const month = '2025-01-01';
    const mockResponse = { id: 1, amount: 100 };

    service.payContribution(month).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpTestingController.expectOne(req => req.url.includes('/api/contributions/pay'));
    expect(req.request.method).toBe('POST');
    expect(req.request.params.get('month')).toBe(month);
    req.flush(mockResponse);
  });
});
