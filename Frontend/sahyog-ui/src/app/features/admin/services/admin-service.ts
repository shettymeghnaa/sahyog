import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private api = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // ── Regions ──────────────────────────────────────────────
  getRegions() { return this.http.get<any[]>(`${this.api}/regions`); }
  createRegion(body: any) { return this.http.post<any>(`${this.api}/regions`, body); }
  getRegionById(id: number) { return this.http.get<any>(`${this.api}/regions/${id}`); }

  // ── Members ──────────────────────────────────────────────
  getAllMembers() { return this.http.get<any[]>(`${this.api}/members`); }
  getMembersByRegion(regionId: number) { return this.http.get<any[]>(`${this.api}/members/region/${regionId}`); }
  suspendMember(id: number) { return this.http.post<any>(`${this.api}/members/suspend/${id}`, {}); }
  activateMember(id: number) { return this.http.post<any>(`${this.api}/members/activate/${id}`, {}); }

  // ── Disasters ────────────────────────────────────────────
  getAllDisasters() { return this.http.get<any[]>(`${this.api}/disasters`); }
  declareDisaster(regionId: number, body: any) { return this.http.post<any>(`${this.api}/disasters/region/${regionId}`, body); }
  closeDisaster(id: number) { return this.http.post<any>(`${this.api}/disasters/close/${id}`, {}); }

  // ── Claims ───────────────────────────────────────────────
  getAllClaims() { return this.http.get<any[]>(`${this.api}/claims`); }

  // ── Policies ─────────────────────────────────────────────
  getPolicyByRegion(regionId: number) { return this.http.get<any>(`${this.api}/policies/region/${regionId}`); }
  createPolicy(regionId: number, body: any) { return this.http.post<any>(`${this.api}/policies/region/${regionId}`, body); }
  updatePolicy(policyId: number, body: any) { return this.http.put<any>(`${this.api}/policies/${policyId}`, body); }

  // ── Pool Fund ────────────────────────────────────────────
  getPoolSummary(regionId: number) { return this.http.get<any>(`${this.api}/pools/region/${regionId}/summary`); }
  getGlobalPoolSummary() { return this.http.get<any>(`${this.api}/pools/summary/global`); }

  // ── Claim Officers ────────────────────────────────────────
  createClaimOfficer(regionId: number, body: any) {
    return this.http.post<any>(`${this.api}/claim-officers/region/${regionId}`, body);
  }
  getClaimOfficers() {
    return this.http.get<any[]>(`${this.api}/claim-officers`);
  }
  deactivateClaimOfficer(id: number) {
    return this.http.put<any>(`${this.api}/claim-officers/${id}/deactivate`, {});
  }
  reassignOfficerRegion(id: number, regionId: number) {
    return this.http.put<any>(`${this.api}/claim-officers/${id}/reassign-region/${regionId}`, {});
  }

  // ── Payouts (Admin Only) ──────────────────────────────────
  payClaim(claimId: number) {
    return this.http.post<any>(`${this.api}/claims/pay/${claimId}`, {});
  }
}
