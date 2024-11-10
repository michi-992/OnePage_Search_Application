import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SearchDataService } from '../../services/search/search-data.service'
import { SearchItem } from '../../models/search-item/search-item.model'
import { StorageService } from '../../services/storage/storage.service';

const API = 'http://localhost:8080/api/searchItems/add'

@Component({
  selector: 'app-search-form',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './search-form.component.html',
  styleUrl: './search-form.component.css'
})
export class SearchFormComponent {
  searchTerm: string = "";

  constructor(private searchDataService: SearchDataService, private storageService: StorageService) { }

  onSearch() {
    const searchItem: SearchItem = { searchTerm: this.searchTerm, user: null };
    this.searchDataService.addSearchItem(searchItem).subscribe(
          response => {
            console.log('POST request successful', response);
          },
          error => {
            console.error('Error in POST request', error);
          }
        );
  }
}
