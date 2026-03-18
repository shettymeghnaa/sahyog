import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { GovernmentService } from './government-service';
import { describe, it, expect, beforeEach } from 'vitest';

describe('GovernmentService', () => {
  let service: GovernmentService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        GovernmentService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(GovernmentService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should update region risk level', () => {
    service.updateRegionRiskLevel(1, 'HIGH').subscribe();

    const req = httpTestingController.expectOne('http://localhost:8080/api/regions/1/risk?level=HIGH');
    expect(req.request.method).toBe('PUT');
    req.flush({});
  });

  it('should declare a disaster', () => {
    const body = { name: 'Fire' };
    service.declareDisaster(1, body).subscribe();

    const req = httpTestingController.expectOne('http://localhost:8080/api/disasters/region/1');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(body);
    req.flush({});
  });
});
