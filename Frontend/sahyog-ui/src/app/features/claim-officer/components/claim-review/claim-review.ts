import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ClaimOfficerService } from '../../services/claim-officer-service';
import { AdminService } from '../../../admin/services/admin-service';

@Component({
  selector: 'app-claim-review',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="min-h-screen bg-[#0a0a0f] text-gray-100 p-8 relative overflow-hidden">
      <!-- Glow -->
      <div class="absolute top-0 right-1/4 w-[500px] h-[500px] bg-[#853953]/10 blur-[150px] rounded-full pointer-events-none"></div>

      <div class="max-w-4xl mx-auto relative z-10">
        <!-- Back Button -->
        <button (click)="goBack()" class="flex items-center gap-2 text-gray-400 hover:text-white transition-colors mb-8 group">
          <svg class="w-5 h-5 group-hover:-translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
          </svg>
          Back to Queue
        </button>

        @if (loading()) {
          <div class="animate-pulse space-y-8">
            <div class="h-10 bg-white/5 rounded-xl w-1/3"></div>
            <div class="grid grid-cols-2 gap-8">
              <div class="h-40 bg-white/5 rounded-2xl"></div>
              <div class="h-40 bg-white/5 rounded-2xl"></div>
            </div>
            <div class="h-60 bg-white/5 rounded-2xl"></div>
          </div>
        } @else if (claim()) {
          <div class="bg-white/[0.03] backdrop-blur-xl border border-white/10 rounded-3xl p-8 md:p-12 shadow-2xl">
            <div class="flex flex-col md:flex-row md:items-center justify-between gap-6 mb-12">
              <div>
                <h2 class="text-4xl font-black text-white tracking-tight mb-2">
                  Claim <span class="text-[#f78cae]">#{{ claim().id }}</span>
                </h2>
                <div class="flex items-center gap-3">
                  <span class="px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-widest border"
                    [ngClass]="{
                      'bg-amber-500/10 text-amber-500 border-amber-500/30': claim().status === 'SUBMITTED',
                      'bg-blue-500/10 text-blue-500 border-blue-500/30': claim().status === 'UNDER_REVIEW',
                      'bg-emerald-500/10 text-emerald-500 border-emerald-500/30': claim().status === 'APPROVED' || claim().status === 'PAID',
                      'bg-rose-500/10 text-rose-500 border-rose-500/30': claim().status === 'REJECTED'
                    }">
                    {{ claim().status }}
                  </span>
                  <span class="text-xs text-gray-500 font-medium">Submitted on {{ claim().claimDate | date:'longDate' }}</span>
                </div>
              </div>
              <div class="text-right">
                <span class="block text-[10px] text-gray-500 uppercase tracking-widest font-bold mb-1">Requested Amount</span>
                <p class="text-3xl font-black text-white">₹ {{ claim().requestedAmount | number:'1.2-2' }}</p>
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-12 mb-12">
              <div class="space-y-8">
                <section>
                  <h4 class="text-[10px] text-gray-500 uppercase tracking-widest font-black mb-3">Member Information</h4>
                  <div class="bg-white/5 border border-white/5 rounded-2xl p-5">
                    <p class="text-lg font-bold text-white mb-1">{{ claim().memberName }}</p>
                    <p class="text-sm text-gray-400 mb-1">{{ claim().memberEmail }}</p>
                    <p class="text-xs text-[#f78cae] font-serif italic">{{ claim().regionName }} Community</p>
                  </div>
                </section>

                <section>
                  <h4 class="text-[10px] text-gray-500 uppercase tracking-widest font-black mb-3">Disaster Context</h4>
                  <div class="bg-white/5 border border-white/5 rounded-2xl p-5">
                    <p class="text-lg font-bold text-white mb-1">{{ claim().disasterType }}</p>
                    <p class="text-sm font-semibold uppercase tracking-wider" 
                       [ngClass]="{
                         'text-orange-400': claim().severityLevel === 'MODERATE',
                         'text-red-500': claim().severityLevel === 'CRITICAL' || claim().severityLevel === 'HIGH'
                       }">
                      {{ claim().severityLevel }} SEVERITY
                    </p>
                  </div>
                </section>
              </div>

              <div class="space-y-8">
                <section>
                  <h4 class="text-[10px] text-gray-500 uppercase tracking-widest font-black mb-3">Damage Description</h4>
                  <div class="bg-black/30 border border-white/5 rounded-2xl p-6 min-h-[140px]">
                    <p class="text-sm text-gray-300 leading-relaxed italic">
                      "{{ claim().description || 'No detailed description provided.' }}"
                    </p>
                  </div>
                </section>

                <section>
                  <h4 class="text-[10px] text-gray-500 uppercase tracking-widest font-black mb-3">Submitted Evidence</h4>
                  @if (claim().documentUrl) {
                    <a [href]="claim().documentUrl" target="_blank" 
                       class="group flex items-center justify-between bg-[#f78cae]/10 hover:bg-[#f78cae]/20 border border-[#f78cae]/30 p-5 rounded-2xl transition-all">
                      <div class="flex items-center gap-4">
                        <div class="w-12 h-12 bg-[#f78cae]/20 rounded-xl flex items-center justify-center text-[#f78cae]">
                          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414A1 1 0 0119 9.414V19a2 2 0 01-2 2z" />
                          </svg>
                        </div>
                        <div>
                          <p class="text-sm font-bold text-white group-hover:text-[#f78cae] transition-colors">Proof Document</p>
                          <p class="text-[10px] text-gray-500 uppercase tracking-tighter">Click to view in new tab</p>
                        </div>
                      </div>
                      <svg class="w-5 h-5 text-gray-500 group-hover:text-white transition-all transform group-hover:translate-x-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3" />
                      </svg>
                    </a>
                  } @else {
                    <div class="bg-red-500/5 border border-red-500/20 p-5 rounded-2xl flex items-center gap-3 text-red-400">
                      <svg class="w-5 h-5 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                      </svg>
                      <p class="text-xs font-semibold">No evidence document provided by member.</p>
                    </div>
                  }
                </section>
              </div>
            </div>

            <hr class="border-white/5 mb-12">

            <!-- Verification Action Box -->
            @if (claim().status === 'UNDER_REVIEW') {
              <div class="bg-white/[0.02] border border-white/10 rounded-3xl p-8 lg:p-10 shadow-inner">
                <h3 class="text-xl font-bold text-white mb-6 flex items-center gap-3">
                  <div class="w-2 h-2 rounded-full bg-[#f78cae] animate-ping"></div>
                  Verification Conclusion
                </h3>
                
                <div class="mb-8">
                  <label class="block text-[10px] text-gray-500 uppercase tracking-widest font-black mb-3">Investigative Notes</label>
                  <textarea 
                    [(ngModel)]="notes"
                    placeholder="Enter your findings, site visit details, or audit notes here..."
                    rows="5"
                    class="w-full bg-black/40 border border-white/5 rounded-2xl p-5 text-white focus:ring-2 focus:ring-[#f78cae]/50 focus:border-[#f78cae] focus:outline-none transition-all resize-none shadow-inner"
                  ></textarea>
                </div>

                <div class="flex flex-col sm:flex-row gap-4">
                  <button 
                    (click)="submitDecision('APPROVE')"
                    [disabled]="isSubmitting || !notes.trim()"
                    class="flex-1 bg-emerald-600 hover:bg-emerald-500 text-white font-bold py-4 rounded-2xl shadow-lg transition-all flex items-center justify-center gap-3 disabled:opacity-40 disabled:cursor-not-allowed">
                    @if (isSubmitting) {
                      <svg class="w-5 h-5 animate-spin" fill="none" viewBox="0 0 24 24">
                        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
                      </svg>
                    }
                    Approve Claim
                  </button>
                  <button 
                    (click)="submitDecision('REJECT')"
                    [disabled]="isSubmitting || !notes.trim()"
                    class="flex-1 border border-rose-500/30 text-rose-500 font-bold py-4 rounded-2xl hover:bg-rose-500/10 transition-all disabled:opacity-40 disabled:cursor-not-allowed">
                    Reject Claim
                  </button>
                </div>
              </div>
            } @else {
              <div class="bg-indigo-500/5 border border-indigo-500/20 p-8 rounded-3xl text-center">
                <p class="text-indigo-300 font-medium">This claim is currently in <strong>{{ claim().status }}</strong> state and cannot be processed further by you.</p>
              </div>
            }
          </div>
        }
      </div>
    </div>
  `,
  styles: []
})
export class ClaimReviewComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private officerService = inject(ClaimOfficerService);
  private adminService = inject(AdminService);

  claim = signal<any>(null);
  loading = signal(true);
  notes: string = '';
  isSubmitting = false;

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadClaim(Number(id));
    }
  }

  loadClaim(id: number) {
    this.officerService.getClaimById(id).subscribe({
      next: (found) => {
        this.claim.set(found);
        // If it's SUBMITTED, auto-move to UNDER_REVIEW
        if (found.status === 'SUBMITTED') {
          this.officerService.reviewClaim(found.id).subscribe({
            next: () => {
              found.status = 'UNDER_REVIEW';
              this.claim.set({ ...found });
            }
          });
        }
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  submitDecision(decision: 'APPROVE' | 'REJECT') {
    if (!this.claim()) return;
    this.isSubmitting = true;

    const action = decision === 'APPROVE' 
      ? this.officerService.approveClaim(this.claim().id, this.notes)
      : this.officerService.rejectClaim(this.claim().id, this.notes);

    action.subscribe({
      next: () => {
        this.isSubmitting = false;
        this.router.navigate(['/claim-officer/dashboard']);
      },
      error: () => {
        this.isSubmitting = false;
        alert('Failed to submit decision. Please try again.');
      }
    });
  }

  goBack() {
    this.router.navigate(['/claim-officer/dashboard']);
  }
}
