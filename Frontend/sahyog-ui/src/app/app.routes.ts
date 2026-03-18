import { Routes } from '@angular/router';

import { LandingComponent } from './features/landing/components/landing-component/landing-component';
import { LoginComponent } from './features/auth/components/login-component/login-component';
import { RegisterComponent } from './features/auth/components/register-component/register-component';

import { MainLayout } from './layout/main-layout/main-layout';

import { Dashboard } from './features/member/components/dashboard/dashboard';
import { PayContribution } from './features/member/components/contributions/pay-contribution/pay-contribution';
import { SubmitClaim } from './features/member/components/claims/submit-claim/submit-claim';

import { ClaimOfficerDashboard } from './features/claim-officer/components/claim-officer-dashboard/claim-officer-dashboard';
import { ClaimReviewComponent } from './features/claim-officer/components/claim-review/claim-review';

// ── Admin ──────────────────────────────────────────────────
import { AdminLayout } from './features/admin/layout/admin-layout';
import { AdminOverview } from './features/admin/pages/overview/admin-overview';
import { AdminRegions } from './features/admin/pages/regions/admin-regions';
import { AdminMembers } from './features/admin/pages/members/admin-members';
import { AdminDisasters } from './features/admin/pages/disasters/admin-disasters';
import { AdminClaims } from './features/admin/pages/claims/admin-claims';
import { AdminPolicies } from './features/admin/pages/policies/admin-policies';
import { AdminClaimOfficers } from './features/admin/pages/claim-officers/admin-claim-officers';

// ── Government ───────────────────────────────────────────
import { GovernmentDashboard } from './features/government/components/government-dashboard/government-dashboard';

import { memberGuard, claimOfficerGuard, adminGuard, governmentGuard } from './features/auth/auth.guard';

export const routes: Routes = [

  { path: '', component: LandingComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  // ── Member Routes ────────────────────────────────────────
  {
    path: '',
    component: MainLayout,
    canActivate: [memberGuard],
    children: [
      { path: 'dashboard', component: Dashboard },
      { path: 'pay-contribution', component: PayContribution },
      { path: 'submit-claim', component: SubmitClaim }
    ]
  },

  // ── Claim Officer Routes ─────────────────────────────────
  {
    path: 'claim-officer',
    canActivate: [claimOfficerGuard],
    children: [
      { path: 'dashboard', component: ClaimOfficerDashboard },
      { path: 'review/:id', component: ClaimReviewComponent }
    ]
  },

  // ── Admin Routes ─────────────────────────────────────────
  {
    path: 'admin',
    component: AdminLayout,
    canActivate: [adminGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: AdminOverview },
      { path: 'regions', component: AdminRegions },
      { path: 'members', component: AdminMembers },
      { path: 'disasters', component: AdminDisasters },
      { path: 'claims', component: AdminClaims },
      { path: 'policies', component: AdminPolicies },
      { path: 'claim-officers', component: AdminClaimOfficers },
    ]
  },

  // ── Government Routes ────────────────────────────────────
  {
    path: 'government',
    canActivate: [governmentGuard],
    children: [
      { path: 'dashboard', component: GovernmentDashboard }
    ]
  },

  { path: '**', redirectTo: '' }

];