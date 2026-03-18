import { Component, inject, signal, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MemberService } from '../../services/member-service';
import { ClaimService } from '../../services/claim-service';
import { ContributionService } from '../../services/contribution-service';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  private memberService: MemberService = inject(MemberService);
  private claimService: ClaimService = inject(ClaimService);
  private contributionService: ContributionService = inject(ContributionService);

  member = signal<any>(null);
  claims = signal<any[]>([]);
  contributions = signal<any[]>([]);
  activeDisasters = signal<any[]>([]);
  allDisasters: any[] = [];

  // Controls which section is displayed in the lower half of the dashboard
  activeView = signal<'impact' | 'claims' | 'contributions'>('impact');

  constructor(private http: HttpClient) { }

  setView(view: 'impact' | 'claims' | 'contributions') {
    this.activeView.set(view);
  }

  filterDisasters() {
    const claimedEventIds = this.claims().map(c => c.disasterId);
    this.activeDisasters.set(this.allDisasters.filter(d => !claimedEventIds.includes(d.id)));
  }

  ngOnInit() {
    this.http.get("http://localhost:8080/api/members/me")
      .subscribe((data: any) => {
        this.member.set(data);
        // Once member is loaded, fetch active disasters for their region
        if (data?.region?.id) {
          this.http.get<any[]>(`http://localhost:8080/api/disasters/active/region/${data.region.id}`)
            .subscribe({ 
              next: d => {
                this.allDisasters = d || [];
                this.filterDisasters();
              }, 
              error: () => {} 
            });
        }
      });

    this.claimService.getMyClaims().subscribe({
      next: (data: any) => {
        this.claims.set(data || []);
        this.filterDisasters();
      },
      error: (err: any) => console.error('Failed to load claims', err)
    });

    this.contributionService.getMyContributions().subscribe({
      next: (data: any) => this.contributions.set(data || []),
      error: (err: any) => console.error('Failed to load contributions', err)
    });
  }
}
