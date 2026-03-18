import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthService } from './services/auth-service';
import { memberGuard, adminGuard, claimOfficerGuard, governmentGuard, authGuard } from './auth.guard';
import { describe, it, expect, beforeEach, vi } from 'vitest';

describe('Auth Guards', () => {
  let authService: any;
  let router: any;

  beforeEach(() => {
    authService = {
      token: vi.fn(),
      isMember: vi.fn(),
      isAdmin: vi.fn(),
      isClaimOfficer: vi.fn(),
      isGovernment: vi.fn(),
    };
    router = {
      navigate: vi.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router },
      ],
    });
  });

  describe('authGuard', () => {
    it('should allow if token is present', () => {
      authService.token.mockReturnValue('token');
      const result = TestBed.runInInjectionContext(() => authGuard({} as any, {} as any));
      expect(result).toBe(true);
    });

    it('should redirect to login if token is absent', () => {
      authService.token.mockReturnValue(null);
      const result = TestBed.runInInjectionContext(() => authGuard({} as any, {} as any));
      expect(result).toBe(false);
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });
  });

  describe('memberGuard', () => {
    it('should allow if token is present and user is member', () => {
      authService.token.mockReturnValue('token');
      authService.isMember.mockReturnValue(true);
      const result = TestBed.runInInjectionContext(() => memberGuard({} as any, {} as any));
      expect(result).toBe(true);
    });

    it('should redirect if user is not member', () => {
      authService.token.mockReturnValue('token');
      authService.isMember.mockReturnValue(false);
      const result = TestBed.runInInjectionContext(() => memberGuard({} as any, {} as any));
      expect(result).toBe(false);
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });
  });

  describe('adminGuard', () => {
    it('should allow if user is admin', () => {
      authService.token.mockReturnValue('token');
      authService.isAdmin.mockReturnValue(true);
      const result = TestBed.runInInjectionContext(() => adminGuard({} as any, {} as any));
      expect(result).toBe(true);
    });
  });
});
