import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SearchDataService } from '../../services/search/search-data.service'
import { SearchItem } from '../../models/search-item/search-item.model'
import { StorageService } from '../../services/storage/storage.service';
import { Subscription } from 'rxjs';

const API = 'http://localhost:8080/api/searchItems/add'

@Component({
  selector: 'app-search-form',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './search-form.component.html',
  styleUrl: './search-form.component.css'
})
export class SearchFormComponent {
  private subscription!: Subscription;
  searchTerm: string = "";

  constructor(private searchDataService: SearchDataService, private storageService: StorageService) { }

  ngOnInit(): void {
    this.subscription = this.searchDataService
      .getSearchTermObservable()
      .subscribe((term) => {
        this.searchTerm = term;
      });
  }


  onSearch() {
    this.searchDataService.onSearch(this.searchTerm);
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe(); // Prevent memory leaks
  }
}
