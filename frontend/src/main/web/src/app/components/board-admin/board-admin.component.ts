import { Component, OnInit } from '@angular/core';
import { TestingService } from '../../services/testing/testing.service';

@Component({
  selector: 'app-board-admin',
  standalone: true,
  imports: [],
  templateUrl: './board-admin.component.html',
  styleUrl: './board-admin.component.css'
})
export class BoardAdminComponent implements OnInit {
  content?: string;

  constructor(private testingService: TestingService) {}

  ngOnInit(): void {
    this.testingService.getAdminContent().subscribe({
      next: data => {
        this.content = data;
      },
      error: err => {
        if(err.error) {
          try {
            const res = JSON.parse(err.error);
            this.content = res.message;
          } catch {
            this.content = `Error with status: ${err.status} - ${err.statusText}`;
          }
        } else {
          this.content = `Error with status: ${err.status}`;
        }
      }
    })
  }
}
