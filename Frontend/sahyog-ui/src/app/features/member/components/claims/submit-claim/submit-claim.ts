import { HttpClient } from '@angular/common/http';
import { Component, signal } from '@angular/core';
import { ClaimService } from '../../../services/claim-service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-submit-claim',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './submit-claim.html',
  styleUrl: './submit-claim.css',
})
export class SubmitClaim {
  /** Only ACTIVE disasters are shown — backend filters them. */
  disasters = signal<any[]>([]);

  disasterId: number = 0;
  requestedAmount: number = 0;
  description: string = '';
  documentUrl: string = '';
  policyMaxPayout: number = 0;

  submitted = false;
  successMsg = '';
  errorMsg = '';

  constructor(
    private http: HttpClient,
    private claimService: ClaimService
  ) {}

  ngOnInit() {
    // Fetch member profile to get regionId, then fetch policy max payout and region-specific disasters
    this.http.get<any>('http://localhost:8080/api/members/me').subscribe({
      next: (m) => {
        if (m.region && m.region.id) {
          // Fetch max payout
          this.http.get<any>(`http://localhost:8080/api/policies/region/${m.region.id}`).subscribe({
            next: (p) => this.policyMaxPayout = p.maxPayoutPerClaim
          });

          // Fetch only ACTIVE disasters for the member's region
          this.http.get<any[]>(`http://localhost:8080/api/disasters/active/region/${m.region.id}`)
            .subscribe({
              next: (d) => this.disasters.set(d),
              error: (err) => console.error('Failed to load active region disasters', err)
            });
        }
      }
    });
  }

  submit() {
    this.errorMsg = '';
    this.successMsg = '';

    if (!this.disasterId || !this.requestedAmount || this.requestedAmount <= 0) {
      this.errorMsg = 'Please select a disaster and enter a valid amount.';
      return;
    }

    if (!this.description || this.description.trim().length < 10) {
      this.errorMsg = 'Please provide a clear description of the damage (at least 10 characters).';
      return;
    }

    if (!this.documentUrl || this.documentUrl.trim().length === 0) {
      this.errorMsg = 'A link to your proof documents is required.';
      return;
    }

    if (this.policyMaxPayout > 0 && this.requestedAmount > this.policyMaxPayout) {
      this.errorMsg = `Requested amount cannot exceed the policy maximum payout of ₹${this.policyMaxPayout}.`;
      return;
    }

    // ⚠️ memberId intentionally NOT sent — server resolves member from JWT
    const body = {
      disasterId: +this.disasterId,
      requestedAmount: this.requestedAmount,
      description: this.description.trim(),
      documentUrl: this.documentUrl.trim()
    };

    this.claimService.submitClaim(body).subscribe({
      next: () => {
        this.submitted = true;
        this.successMsg = 'Claim submitted successfully! It will be reviewed by a claim officer.';
      },
      error: (err) => {
        const serverMsg = err?.error?.message
          || err?.error?.error
          || err?.message
          || 'Failed to submit claim. Please try again.';
        this.errorMsg = `Error: ${serverMsg}`;
        // Print the full body so the reason is visible in DevTools
        console.error('Claim submit failed — server response body:', err?.error);
        console.error('HTTP status:', err?.status, '| statusText:', err?.statusText);
      }
    });
  }
}
