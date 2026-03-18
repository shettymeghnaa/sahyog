import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { ContributionService } from '../../../services/contribution-service';

@Component({
  selector: 'app-pay-contribution',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './pay-contribution.html',
  styleUrl: './pay-contribution.css'
})
export class PayContribution implements OnInit {
  month: string = new Date().toISOString().substring(0, 7);
  monthDisplay = signal<Date>(new Date());
  loading = signal(false);
  successMsg = signal('');
  errorMsg = signal('');
  
  // UI States for UX
  showConfirmation = signal(false);
  memberInfo = signal<any>(null);
  calculatedAmount = signal<number>(0);

  constructor(
    private contributionService: ContributionService,
    private http: HttpClient
  ) {}

  ngOnInit() {
    this.updateMonthDisplay();
    this.http.get<any>('http://localhost:8080/api/members/me').subscribe({
      next: (member) => {
        this.memberInfo.set(member);
        
        // Fetch policy to calculate exact amount since Region might not eagerly load it on the Member DTO
        if (member?.region?.id) {
          this.http.get<any>(`http://localhost:8080/api/policies/region/${member.region.id}`).subscribe({
            next: (policy) => {
              if (policy && policy.monthlyContribution) {
                let amount = policy.monthlyContribution;
                const risk = member.region.riskLevel?.toUpperCase();
                
                if (risk === 'MEDIUM') amount *= 1.25;
                else if (risk === 'HIGH' || risk === 'CRITICAL') amount *= 1.50;
                
                this.calculatedAmount.set(amount);
              }
            }
          });
        }
      }
    });
  }

  initiatePayment() {
    this.errorMsg.set('');
    this.successMsg.set('');

    if (!this.month) {
      this.errorMsg.set('Please select a month before paying.');
      return;
    }

    const formattedMonth = `${this.month}-01`;
    const currentMonth = new Date().toISOString().substring(0, 7) + '-01';
    
    if (formattedMonth !== currentMonth) {
      this.errorMsg.set('Contributions can only be paid for the current month.');
      return;
    }

    this.showConfirmation.set(true);
  }

  cancelPayment() {
    this.showConfirmation.set(false);
  }

  private updateMonthDisplay() {
    const [year, monthStr] = this.month.split('-');
    this.monthDisplay.set(new Date(parseInt(year), parseInt(monthStr) - 1, 1));
  }

  confirmAndPay() {
    this.showConfirmation.set(false);
    this.loading.set(true);
    const formattedMonth = `${this.month}-01`;

    this.contributionService.payContribution(formattedMonth).subscribe({
      next: () => {
        this.loading.set(false);
        this.successMsg.set(`Contribution for ${this.month} paid successfully! ✅`);
        // Reset month to current month instead of empty string
        this.month = new Date().toISOString().substring(0, 7);
        this.monthDisplay.set(new Date());
      },
      error: err => {
        this.loading.set(false);
        this.errorMsg.set(err?.error?.message || 'Payment failed. Please try again.');
      }
    });
  }
}