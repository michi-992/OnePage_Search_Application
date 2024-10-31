import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SearchDataService } from '../../services/search/search-data.service'
import { SearchItem } from '../../models/search-item/search-item.model'
import { Observable } from 'rxjs';

@Component({
  selector: 'app-search-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './search-history.component.html',
  styleUrl: './search-history.component.css'
})
export class SearchHistoryComponent {
  searchItems$: Observable<SearchItem[]>;
  constructor(private searchDataService: SearchDataService) {
      this.searchItems$ = this.searchDataService.getSearchItems();
    }
}
