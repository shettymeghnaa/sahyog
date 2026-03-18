import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ContributionService {
   private api = "http://localhost:8080/api/contributions";

  constructor(private http: HttpClient) {}

  payContribution(month: string){
    const params = new HttpParams().set('month', month);
    return this.http.post(`${this.api}/pay`, {}, { params });
  }

 getMyContributions() {
  return this.http.get(`${this.api}/my`);
}
}
