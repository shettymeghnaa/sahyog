import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { AdminService } from './admin-service';
import { describe, it, expect, beforeEach } from 'vitest';

describe('AdminService', () => {
  let service: AdminService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AdminService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(AdminService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch regions', () => {
    const mockRegions = [{ id: 1, name: 'North' }];
    service.getRegions().subscribe(res => expect(res).toEqual(mockRegions));

    const req = httpTestingController.expectOne('http://localhost:8080/api/regions');
    expect(req.request.method).toBe('GET');
    req.flush(mockRegions);
  });

  it('should suspend a member', () => {
    service.suspendMember(1).subscribe();
    const req = httpTestingController.expectOne('http://localhost:8080/api/members/suspend/1');
    expect(req.request.method).toBe('POST');
    req.flush({});
  });

  it('should fetch global pool summary', () => {
    const mockSummary = { totalBalance: 10000 };
    service.getGlobalPoolSummary().subscribe(res => expect(res).toEqual(mockSummary));

    const req = httpTestingController.expectOne('http://localhost:8080/api/pools/summary/global');
    req.flush(mockSummary);
  });
});
