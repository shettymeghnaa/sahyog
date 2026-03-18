import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login-component';
import { AuthService } from '../../services/auth-service';
import { provideRouter } from '@angular/router';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { FormsModule } from '@angular/forms';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: any;

  beforeEach(async () => {
    authService = {
      login: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [LoginComponent, FormsModule],
      providers: [
        { provide: AuthService, useValue: authService },
        provideRouter([])
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show error if fields are empty', () => {
    component.username = '';
    component.password = '';
    component.login();
    expect(component.errorMsg()).toBe('Please enter your email and password.');
  });

  it('should show error for invalid email format', () => {
    component.username = 'invalid-email';
    component.password = 'password';
    component.login();
    expect(component.errorMsg()).toBe('Please enter a valid email address.');
  });

  it('should call authService.login for valid input', () => {
    component.username = 'test@example.com';
    component.password = 'password123';
    component.login();
    
    expect(component.errorMsg()).toBe('');
    expect(component.loading()).toBe(true);
    expect(authService.login).toHaveBeenCalledWith(
      'test@example.com', 
      'password123', 
      expect.any(Object)
    );
  });
});
