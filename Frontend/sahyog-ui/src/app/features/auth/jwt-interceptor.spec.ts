import { TestBed } from '@angular/core/testing';
import { provideHttpClient, withInterceptors, HttpRequest, HttpHandlerFn } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { HttpClient, provideHttpClient as provideNgHttpClient, withInterceptors as withNgInterceptors } from '@angular/common/http';
import { jwtInterceptor } from './jwt-interceptor';
import { describe, it, expect, beforeEach, vi } from 'vitest';

describe('jwtInterceptor', () => {
  let httpTestingController: HttpTestingController;
  let httpClient: HttpClient;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideNgHttpClient(withNgInterceptors([jwtInterceptor])),
        provideHttpClientTesting(),
      ],
    });

    httpClient = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

  it('should add Authorization header when token is present', () => {
    localStorage.setItem('token', 'test-token');

    httpClient.get('/test').subscribe();

    const req = httpTestingController.expectOne('/test');
    expect(req.request.headers.has('Authorization')).toBe(true);
    expect(req.request.headers.get('Authorization')).toBe('Bearer test-token');
  });

  it('should NOT add Authorization header when token is absent', () => {
    httpClient.get('/test').subscribe();

    const req = httpTestingController.expectOne('/test');
    expect(req.request.headers.has('Authorization')).toBe(false);
  });
});
