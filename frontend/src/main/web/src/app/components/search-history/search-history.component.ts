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
  // searchItems$: Observable<SearchItem[]>;
  // private historyUpdateSub: Subscription | null = null;

  // constructor(private searchDataService: SearchDataService) {
  //   this.searchItems$ = new Observable<SearchItem[]>()
  //   }

  // ngOnInit(): void {
  //   this.historyUpdateSub = this.searchDataService.getSearchUpdatedListener().subscribe(() => {
  //     this.loadSearchHistory();
  //   });
  //   this.loadSearchHistory();
  // }

  //  ngOnDestroy(): void {
  //    if (this.historyUpdateSub) {
  //      this.historyUpdateSub.unsubscribe();
  //    }
  //  }

  //  private loadSearchHistory(): void {
  //   this.searchDataService.searchRecipesByTitleContaining(
  //     this.searchDataService.getSearchTerm(),
  //     this.searchDataService.getUsername(),
  //     this.searchDataService.getPage(),
  //     this.searchDataService.getSize()
  //   ).subscribe({
  //     next: (response) => {
  //       this.searchItems$ = new Observable<SearchItem[]>(observer => {
  //         observer.next(response.searchHistory);
  //         observer.complete();
  //       });
  //     },
  //     error: (err) => console.error(err),
  //   });
  // }

  searchItems$: Observable<SearchItem[]>;

  constructor(private searchDataService: SearchDataService) {
    this.searchItems$ = this.searchDataService.getSearchHistory();
  }

  ngOnInit(): void {}
}
