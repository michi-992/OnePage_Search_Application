import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SearchFormComponent } from './components/search-form/search-form.component';
import { SearchHistoryComponent } from './components/search-history/search-history.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NgbModule, SearchFormComponent, SearchHistoryComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'frontendApp';
}
