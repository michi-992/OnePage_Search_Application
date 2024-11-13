import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SearchDataService } from '../../services/search/search-data.service';
import { SearchItem } from '../../models/search-item/search-item.model';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-full-search-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './full-search-list.component.html',
  styleUrl: './full-search-list.component.css'
})
export class FullSearchListComponent {
  searchItems$: Observable<any>;
  expandedStates: { [key: number]: boolean } = {};

  constructor(private searchDataService: SearchDataService) {
      this.searchItems$ = this.searchDataService.getAllSearchItemsGroupedByUser();
    }

  toggleUserItems(userId: number): void {
      this.expandedStates[userId] = !this.expandedStates[userId];
    }

  getExpandedState(userId: number): Boolean {
    return this.expandedStates[userId];
    }

}
