import { Component } from '@angular/core';
import { SearchFormComponent } from '../search-form/search-form.component'
import { SearchHistoryComponent } from '../search-history/search-history.component'
import { RecipeListComponent } from '../recipe-list/recipe-list.component'

@Component({
  selector: 'app-search-page',
  standalone: true,
  imports: [SearchFormComponent, SearchHistoryComponent, RecipeListComponent],
  templateUrl: './search-page.component.html',
  styleUrl: './search-page.component.css'
})
export class SearchPageComponent {

}
