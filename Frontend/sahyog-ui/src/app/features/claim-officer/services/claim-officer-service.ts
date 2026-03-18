import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ClaimOfficerService {
  private api = 'http://localhost:8080/api/claims';

  constructor(private http: HttpClient) {}

  /** Fetch all claims with PENDING status for the review queue. */
  getPendingClaims() {
    return this.http.get<any[]>(`${this.api}/pending`);
  }

  getClaimById(id: number) {
    return this.http.get<any>(`${this.api}/${id}`);
  }

  /** Trigger a review to move the claim from SUBMITTED to UNDER_REVIEW */
  reviewClaim(claimId: number) {
    return this.http.post<any>(`${this.api}/review/${claimId}`, {});
  }

  /** Approve a pending claim. Officer username is sent via JWT. */
  approveClaim(claimId: number, verificationNotes: string) {
    return this.http.post<any>(`${this.api}/approve/${claimId}`, { verificationNotes });
  }

  /** Reject a pending claim. Officer username is sent via JWT. */
  rejectClaim(claimId: number, verificationNotes: string) {
    return this.http.post<any>(`${this.api}/reject/${claimId}`, { verificationNotes });
  }
}
