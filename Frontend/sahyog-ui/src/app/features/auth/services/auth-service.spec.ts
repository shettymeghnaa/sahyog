import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService } from './auth-service';
import { describe, it, expect, beforeEach, vi } from 'vitest';

describe('AuthService', () => {
  let service: AuthService;
  let httpTestingController: HttpTestingController;
  let router: Router;

  beforeEach(() => {
    const routerMock = {
      navigate: vi.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: Router, useValue: routerMock },
      ],
    });

    service = TestBed.inject(AuthService);
    httpTestingController = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);

    // Clear localStorage before each test
    localStorage.clear();
    service.token.set(null);
    service.role.set(null);
    service.isLoggedIn.set(false);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should login and navigate to dashboard on success', () => {
    const mockResponse = { token: 'test-token', role: 'ROLE_ADMIN' };
    const username = 'testuser';
    const password = 'password';

    service.login(username, password);

    const req = httpTestingController.expectOne('http://localhost:8080/api/auth/login');
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);

    expect(localStorage.getItem('token')).toBe('test-token');
    expect(localStorage.getItem('role')).toBe('ROLE_ADMIN');
    expect(service.token()).toBe('test-token');
    expect(service.role()).toBe('ROLE_ADMIN');
    expect(service.isLoggedIn()).toBe(true);
    expect(router.navigate).toHaveBeenCalledWith(['/admin/dashboard']);
  });

  it('should handle login error and call onError callback', () => {
    const username = 'testuser';
    const password = 'wrong-password';
    const errorCallback = vi.fn();

    service.login(username, password, { onError: errorCallback });

    const req = httpTestingController.expectOne('http://localhost:8080/api/auth/login');
    req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

    expect(errorCallback).toHaveBeenCalledWith('Invalid email or password.');
    expect(service.isLoggedIn()).toBe(false);
  });

  it('should logout and navigate to login page', () => {
    localStorage.setItem('token', 'test-token');
    localStorage.setItem('role', 'ROLE_ADMIN');
    service.token.set('test-token');
    service.role.set('ROLE_ADMIN');
    service.isLoggedIn.set(true);

    service.logout();

    expect(localStorage.getItem('token')).toBeNull();
    expect(localStorage.getItem('role')).toBeNull();
    expect(service.token()).toBeNull();
    expect(service.isLoggedIn()).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should return correct role checks', () => {
    service.role.set('ROLE_ADMIN');
    expect(service.isAdmin()).toBe(true);
    expect(service.isMember()).toBe(false);

    service.role.set('ROLE_MEMBER');
    expect(service.isMember()).toBe(true);
    expect(service.isAdmin()).toBe(false);
  });
});
