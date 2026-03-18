import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { MemberService } from './member-service';
import { describe, it, expect, beforeEach } from 'vitest';
import { Member } from '../../shared/models/member';

describe('MemberService', () => {
  let service: MemberService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        MemberService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(MemberService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should load member profile into signal', () => {
    const mockMember: Member = {
      id: 1,
      fullName: 'John Doe',
      email: 'john@example.com',
      status: 'ACTIVE'
    } as any;

    service.loadProfile();

    const req = httpTestingController.expectOne('http://localhost:8080/api/members/me');
    expect(req.request.method).toBe('GET');
    req.flush(mockMember);

    expect(service.member()).toEqual(mockMember);
  });
});
