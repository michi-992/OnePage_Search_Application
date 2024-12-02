import { Component } from '@angular/core';
import { SearchFormComponent } from '../search-form/search-form.component'
import { SearchHistoryComponent } from '../search-history/search-history.component'
import { RecipeListComponent } from '../recipe-list/recipe-list.component'
import { SortingFilteringComponent } from '../sorting-filtering/sorting-filtering.component';

@Component({
  selector: 'app-search-page',
  standalone: true,
  imports: [SearchFormComponent, SearchHistoryComponent, RecipeListComponent, SortingFilteringComponent],
  templateUrl: './search-page.component.html',
  styleUrl: './search-page.component.css'
})
export class SearchPageComponent {

}
