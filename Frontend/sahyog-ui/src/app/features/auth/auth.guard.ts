import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './services/auth-service';

/**
 * Generic authenticated guard — redirects to /login if no token present.
 */
export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.token()) {
    router.navigate(['/login']);
    return false;
  }
  return true;
};

/**
 * Member-only route guard.
 */
export const memberGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.token()) {
    router.navigate(['/login']);
    return false;
  }
  if (!auth.isMember()) {
    router.navigate(['/login']);
    return false;
  }
  return true;
};

/**
 * Claim Officer-only route guard.
 */
export const claimOfficerGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.token()) {
    router.navigate(['/login']);
    return false;
  }
  if (!auth.isClaimOfficer()) {
    router.navigate(['/login']);
    return false;
  }
  return true;
};

/**
 * Admin-only route guard.
 */
export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.token()) {
    router.navigate(['/login']);
    return false;
  }
  if (!auth.isAdmin()) {
    router.navigate(['/login']);
    return false;
  }
  return true;
};

/**
 * Government-only route guard.
 */
export const governmentGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.token()) {
    router.navigate(['/login']);
    return false;
  }
  if (!auth.isGovernment()) {
    router.navigate(['/login']);
    return false;
  }
  return true;
};
