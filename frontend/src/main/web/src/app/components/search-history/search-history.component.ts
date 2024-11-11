import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SearchDataService } from '../../services/search/search-data.service'
import { SearchItem } from '../../models/search-item/search-item.model'
import { Observable, Subscription } from 'rxjs';

@Component({
  selector: 'app-search-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './search-history.component.html',
  styleUrl: './search-history.component.css'
})
export class SearchHistoryComponent {
  searchItems$: Observable<any[]>;
  private searchUpdateSub: Subscription | null = null;

  constructor(private searchDataService: SearchDataService) {
      this.searchItems$ = this.searchDataService.getSearchItemsByUser();
    }

  ngOnInit(): void {
    this.searchUpdateSub = this.searchDataService.getSearchUpdatedListener().subscribe(() => {
      this.searchItems$ = this.searchDataService.getSearchItemsByUser();
    });
  }

   ngOnDestroy(): void {
     if (this.searchUpdateSub) {
       this.searchUpdateSub.unsubscribe();
     }
   }
}
