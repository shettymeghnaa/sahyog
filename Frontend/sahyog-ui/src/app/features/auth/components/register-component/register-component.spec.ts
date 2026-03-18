import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RegisterComponent } from './register-component';
import { HttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { FormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let httpTestingController: HttpTestingController;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisterComponent, FormsModule],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    httpTestingController = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate');
    fixture.detectChanges();

    // Handle initial regions load
    const req = httpTestingController.expectOne('http://localhost:8080/api/regions');
    req.flush([{ id: 1, name: 'North' }]);
  });

  it('should create and load regions', () => {
    expect(component).toBeTruthy();
    expect(component.regions.length).toBe(1);
    expect(component.regions[0].name).toBe('North');
  });

  it('should show error if any field is missing', () => {
    component.fullName = '';
    component.register();
    expect(component.errorMsg()).toBe('Please fill in all fields.');
  });

  it('should validate email format', () => {
    component.fullName = 'John Doe';
    component.email = 'bad-email';
    component.password = 'password';
    component.register();
    expect(component.errorMsg()).toBe('Please enter a valid email address.');
  });

  it('should validate password match', () => {
    component.fullName = 'John Doe';
    component.email = 'john@example.com';
    component.phoneNumber = '1234567890';
    component.password = 'password123';
    component.confirmPassword = 'mismatch';
    component.register();
    expect(component.errorMsg()).toBe('Passwords do not match.');
  });

  it('should call API on valid registration', () => {
    component.fullName = 'John Doe';
    component.email = 'john@example.com';
    component.phoneNumber = '1234567890';
    component.password = 'password123';
    component.confirmPassword = 'password123';
    component.regionId = 1;
    component.acceptedTerms = true;

    component.register();

    const req = httpTestingController.expectOne('http://localhost:8080/api/members/region/1');
    expect(req.request.method).toBe('POST');
    req.flush({});

    expect(component.loading()).toBe(false);
    expect(component.isSuccessView()).toBe(true);
  });
});
