import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';
import { StorageService } from '../../services/storage/storage.service';
import { LoginData } from '../../models/login/login.model'
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { User } from '../../models/user/user.model';

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
  username: string = '';

  constructor(private authService: AuthService, private storageService: StorageService) {}

  ngOnInit(): void {
    if(this.storageService.isLoggedIn()) {
      this.isLoggedIn;
      this.username = this.storageService.getUser().username;
    }
  }

  onSubmit(): void {
    this.authService.login(this.login).subscribe({
      next: data => {
        const user: User = {
          id: data.id,
          username: data.username,
          email: data.username,
          roles: data.roles,
        };
        this.storageService.saveUser(user);

        this.isLoginFailed = false;
        this.isLoggedIn = true;
        this.username = this.storageService.getUser().username;
        this.reloadPage();
      },
    error: err => {
      this.errorMessage = err.error.message;
      console.log(this.errorMessage);
      console.log(err.error.message);
      this.isLoginFailed = true;
      }
    })
  }

  reloadPage(): void {
    window.location.reload();
  }

}
