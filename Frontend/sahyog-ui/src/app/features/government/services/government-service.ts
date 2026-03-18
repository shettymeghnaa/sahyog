import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class GovernmentService {
  private api = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // ── Regions ──────────────────────────────────────────────
  getRegions() { return this.http.get<any[]>(`${this.api}/regions`); }
  updateRegionRiskLevel(id: number, level: string) { return this.http.put<any>(`${this.api}/regions/${id}/risk?level=${level}`, {}); }

  // ── Disasters ────────────────────────────────────────────
  getAllDisasters() { return this.http.get<any[]>(`${this.api}/disasters`); }
  declareDisaster(regionId: number, body: any) { return this.http.post<any>(`${this.api}/disasters/region/${regionId}`, body); }
  closeDisaster(id: number) { return this.http.post<any>(`${this.api}/disasters/close/${id}`, {}); }
}
