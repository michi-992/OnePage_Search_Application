import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoginData } from '../../models/login/login.model'
import { RegistrationData } from '../../models/register/registration.model'

const AUTH_API = 'http://localhost:8080/api/auth/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  withCredentials: true
};

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) {}

  login(loginData: LoginData): Observable<any> {
    return this.http.post(
      AUTH_API + 'signin',
      {
        username: loginData.username,
        password: loginData.password,
      },
      httpOptions
    );
  }

  register(registerData: RegistrationData): Observable<any> {
    return this.http.post(
      AUTH_API + 'signup',
      {
        username: registerData.username,
        email: registerData.email,
        password: registerData.password,
      },
      httpOptions
    );
  }

  logout(): Observable<any> {
    return this.http.post(AUTH_API + 'signout', { }, httpOptions);
  }
}
