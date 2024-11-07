import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/api/test/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  responseType: 'text' as 'json',
  withCredentials: true
};


@Injectable({
  providedIn: 'root'
})
export class TestingService {

  constructor(private http: HttpClient) { }

  getPublicContent(): Observable<any> {
    return this.http.get(API_URL + 'all', httpOptions);
  }

  getUserContent(): Observable<any> {
    return this.http.get(API_URL + 'user', httpOptions);
  }

  getAdminContent(): Observable<any> {
    return this.http.get(API_URL + 'admin', httpOptions);
  }

}
