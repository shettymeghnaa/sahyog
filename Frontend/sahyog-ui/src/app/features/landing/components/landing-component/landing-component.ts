import { Component, OnInit, OnDestroy, HostListener, Inject, PLATFORM_ID, ElementRef, ViewChildren, QueryList, AfterViewInit, ChangeDetectorRef } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { isPlatformBrowser, CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../auth/services/auth-service';

@Component({
  selector: 'app-landing-component',
  imports: [RouterLink, CommonModule],
  templateUrl: './landing-component.html',
  styleUrl: './landing-component.css',
})
export class LandingComponent implements OnInit, OnDestroy, AfterViewInit {
  // Stats
  membersProtected = 0;
  fundsDistributed = 0;
  regionsCovered = 0;
  avgClaimTime = 0;

  // Dynamic backend data
  regions: any[] = [];

  private observer: IntersectionObserver | null = null;

  @ViewChildren('animatedSection') animatedSections!: QueryList<ElementRef>;

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private http: HttpClient,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      if (this.authService.isLoggedIn()) {
        if (this.authService.isAdmin()) {
          this.router.navigate(['/admin/dashboard']);
        } else if (this.authService.isClaimOfficer()) {
          this.router.navigate(['/claim-officer/dashboard']);
        } else if (this.authService.isGovernment()) {
          this.router.navigate(['/government/dashboard']);
        } else {
          this.router.navigate(['/dashboard']);
        }
        return;
      }

      this.fetchRegions();
    }
  }

  fetchRegions() {
    this.http.get<any[]>('http://localhost:8080/api/regions').subscribe({
      next: (data) => {
        this.regions = data;
        // Optionally update region counter dynamically based on actual response
        // if (data && data.length > 0) this.regionsCovered = data.length;
      },
      error: (err) => console.error('Failed to fetch regions', err)
    });
  }

  ngAfterViewInit() {
    if (isPlatformBrowser(this.platformId)) {
      this.setupIntersectionObserver();
    }
  }

  ngOnDestroy() {
    if (this.observer) {
      this.observer.disconnect();
    }
  }

  // Smooth scroll method for "See How It Works" button
  scrollToHowItWorks() {
    if (isPlatformBrowser(this.platformId)) {
      const el = document.getElementById('how-it-works');
      if (el) {
        el.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }
    }
  }

  setupIntersectionObserver() {
    const options = {
      root: null,
      rootMargin: '0px',
      threshold: 0.15
    };

    if (typeof IntersectionObserver !== 'undefined') {
      this.observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
          if (entry.isIntersecting) {
            entry.target.classList.add('visible');

            if (entry.target.classList.contains('stats-trigger')) {
              this.animateStats();
              entry.target.classList.remove('stats-trigger'); // Only animate once
            }
          }
        });
      }, options);

      this.animatedSections.forEach(section => {
        this.observer?.observe(section.nativeElement);
      });
    }
  }

  animateStats() {
    this.animateValue('membersProtected', 12482, 2000);
    this.animateValue('fundsDistributed', 34000000, 2500); // 3.4 Crores
    this.animateValue('regionsCovered', 18, 1500);
    this.animateValue('avgClaimTime', 36, 1500);
  }

  animateValue(prop: string, end: number, duration: number) {
    let startTimestamp: number | null = null;
    const step = (timestamp: number) => {
      if (!startTimestamp) startTimestamp = timestamp;
      const progress = Math.min((timestamp - startTimestamp) / duration, 1);
      const easeProgress = progress === 1 ? 1 : 1 - Math.pow(2, -10 * progress);

      const currentValue = Math.floor(easeProgress * end);

      if (prop === 'membersProtected') this.membersProtected = currentValue;
      if (prop === 'fundsDistributed') this.fundsDistributed = currentValue;
      if (prop === 'regionsCovered') this.regionsCovered = currentValue;
      if (prop === 'avgClaimTime') this.avgClaimTime = currentValue;

      this.cdr.detectChanges(); // Trigger change detection for animated values

      if (progress < 1) {
        window.requestAnimationFrame(step);
      } else {
        if (prop === 'membersProtected') this.membersProtected = end;
        if (prop === 'fundsDistributed') this.fundsDistributed = end;
        if (prop === 'regionsCovered') this.regionsCovered = end;
        if (prop === 'avgClaimTime') this.avgClaimTime = end;
        this.cdr.detectChanges(); // Final change detection pass
      }
    };
    window.requestAnimationFrame(step);
  }
}
