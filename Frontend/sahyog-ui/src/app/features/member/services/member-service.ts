import { Injectable, signal } from '@angular/core';

import { HttpClient } from '@angular/common/http';
import { Member } from '../../shared/models/member';

@Injectable({
  providedIn: 'root',
})
export class MemberService {
  private api = 'http://localhost:8080/api/members';

  member = signal<Member | null>(null);

  constructor(private http: HttpClient){}

  loadProfile(){

    this.http.get<Member>(`${this.api}/me`)
    .subscribe(res => {

      this.member.set(res);

    });

  }
}
