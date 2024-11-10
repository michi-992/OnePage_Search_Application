import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';
import { StorageService } from '../../services/storage/storage.service';
import { LoginData } from '../../models/login/login.model'
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login-form',
  standalone: true,
  imports: [RouterModule, FormsModule, CommonModule],
  templateUrl: './login-form.component.html',
  styleUrl: './login-form.component.css'
})
export class LoginFormComponent implements OnInit{
  login: LoginData = { username: null, password: null };

  isLoggedIn = false;
  isLoginFailed = false;
  errorMessage = '';
  roles: string[] = [];

  constructor(private authService: AuthService, private storageService: StorageService) {}

  ngOnInit(): void {
    if(this.storageService.isLoggedIn()) {
      this.isLoggedIn;
      this.roles = this.storageService.getUser().roles;
    }
  }

  onSubmit(): void {
    this.authService.login(this.login).subscribe({
      next: data => {
        this.storageService.saveUser(data);

        this.isLoginFailed = false;
        this.isLoggedIn = true;
        this.roles = this.storageService.getUser().roles;
        this.reloadPage();
      }
    })
  }

  reloadPage(): void {
    window.location.reload();
  }

}
