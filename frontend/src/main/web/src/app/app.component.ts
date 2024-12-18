import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterModule } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { Subscription } from 'rxjs';
import { StorageService } from './services/storage/storage.service';
import { AuthService } from './services/auth/auth.service';
import { EventBusService } from './shared/event-bus.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterModule, NgbModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  private roles: string[] = [];
    isLoggedIn = false;
    showAdminBoard = false;
    showModeratorBoard = false;
    username?: string;

    eventBusSub?: Subscription;
    loggedInSub: Subscription | null = null;

    constructor(
      private storageService: StorageService,
      private authService: AuthService,
      private eventBusService: EventBusService
    ) {}

    ngOnInit(): void {
      this.isLoggedIn = this.storageService.isLoggedIn();

      if (this.isLoggedIn) {
        const user = this.storageService.getUser();
        this.roles = user.roles;

        this.showAdminBoard = this.roles.includes('ROLE_ADMIN');
        this.showModeratorBoard = this.roles.includes('ROLE_MODERATOR');

        this.username = user.username;
      }

      this.eventBusSub = this.eventBusService.on('logout', () => {
        this.logout();
      });

    this.loggedInSub = this.authService.getIsLoggedInListener().subscribe(isLoggedIn => {
          this.isLoggedIn = isLoggedIn;
        });
    }

    logout(): void {
      this.authService.logout().subscribe({
        next: res => {
          this.storageService.clean();
          window.location.reload();
        },
        error: err => {
          console.log(err);
        }
      });
    }
  }
