import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ClaimService {
   private api = "http://localhost:8080/api/claims";

  constructor(private http: HttpClient) {}

  submitClaim(body:any){
    return this.http.post(`${this.api}/submit`, body);
  }

  getMyClaims(){
    return this.http.get(`${this.api}/my`);
  }
}
