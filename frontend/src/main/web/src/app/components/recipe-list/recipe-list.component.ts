import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SearchDataService } from '../../services/search/search-data.service'
import { Recipe } from '../../models/recipe/recipe.model'
import { Observable, Subject, Subscription } from 'rxjs';

@Component({
  selector: 'app-recipe-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './recipe-list.component.html',
  styleUrl: './recipe-list.component.css'
})
export class RecipeListComponent {
  recipes$: Observable<Recipe[]>;
  private searchUpdateSub: Subscription | null = null;

  constructor(private searchDataService: SearchDataService) {
    this.recipes$ = this.searchDataService.searchRecipesByTitleContaining("");
  }

  ngOnInit(): void {
    this.searchUpdateSub = this.searchDataService.getSearchUpdatedListener().subscribe(() => {
      this.recipes$ = this.searchDataService.searchRecipesByTitleContaining(this.searchDataService.getSearchTerm());
    });
  }

  ngOnDestroy(): void {
    if (this.searchUpdateSub) {
      this.searchUpdateSub.unsubscribe();
    }
  }
}
