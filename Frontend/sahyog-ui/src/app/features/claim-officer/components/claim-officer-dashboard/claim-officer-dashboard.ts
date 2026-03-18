import { Component, signal, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ClaimOfficerService } from '../../services/claim-officer-service';
import { AuthService } from '../../../auth/services/auth-service';

@Component({
  selector: 'app-claim-officer-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './claim-officer-dashboard.html',
  styleUrl: './claim-officer-dashboard.css',
})
export class ClaimOfficerDashboard implements OnInit {
  private claimOfficerService = inject(ClaimOfficerService);
  private authService = inject(AuthService);
  private router = inject(Router);

  pendingClaims = signal<any[]>([]);
  assignedClaims = signal<any[]>([]);
  loading = signal<boolean>(true);
  error = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  ngOnInit() {
    this.loadPendingClaims();
  }

  loadPendingClaims() {
    this.loading.set(true);
    this.error.set(null);

    this.claimOfficerService.getPendingClaims().subscribe({
      next: (claims) => {
        this.pendingClaims.set(claims);
        this.assignedClaims.set(claims);
        this.loading.set(false);
      },
      error: (err) => {
        console.error(err);
        this.error.set('Failed to load pending claims.');
        this.loading.set(false);
      }
    });
  }

  // Called when user clicks "Review" in the table
  review(claim: any) {
    this.router.navigate(['/claim-officer/review', claim.id]);
  }

  logout() {
    if (confirm('Are you sure you want to log out?')) {
      this.authService.logout();
    }
  }
}
