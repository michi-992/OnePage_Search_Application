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
  // recipes$: Observable<Recipe[]>;
  // private searchUpdateSub: Subscription | null = null;

  // constructor(private searchDataService: SearchDataService) {
  //   this.recipes$ = new Observable<Recipe[]>();
  // }

  // ngOnInit(): void {
  //   this.searchUpdateSub = this.searchDataService.getSearchUpdatedListener().subscribe(() => {
  //     this.loadRecipes();
  //   });
  //   this.loadRecipes();
  // }

  // ngOnDestroy(): void {
  //   if (this.searchUpdateSub) {
  //     this.searchUpdateSub.unsubscribe();
  //   }
  // }

  // private loadRecipes(): void {
  //   this.searchDataService.searchRecipesByTitleContaining(
  //     this.searchDataService.getSearchTerm(),
  //     this.searchDataService.getUsername(),
  //     this.searchDataService.getPage(),
  //     this.searchDataService.getSize()
  //   ).subscribe({
  //     next: (response) => {
  //       this.recipes$ = new Observable<Recipe[]>(observer => {
  //         observer.next(response.searchResponse.hits);
  //         observer.complete();
  //       });
  //     },
  //     error: (err) => console.error(err),
  //   });
  // }

  recipes$: Observable<Recipe[]>;
  currentPage: number = 1;
  totalPages: number = 0;
  totalPagesArray: number[] = [];

  constructor(private searchDataService: SearchDataService) {
    this.recipes$ = this.searchDataService.getRecipes();
  }

  ngOnInit(): void {
    this.searchDataService.getTotalHitsObservable().subscribe((totalHits) => {
      this.calculatePagination(totalHits);
    });
  }

  displayedPages: number[] = [];

  calculatePagination(totalHits: number): void {
  const size = this.searchDataService.getSize();
  this.currentPage = this.searchDataService.getPage();
  this.totalPages = Math.ceil(totalHits / size);

  const startPage = Math.max(this.currentPage - 2, 1);
  const endPage = Math.min(this.currentPage + 2, this.totalPages);

  this.displayedPages = Array.from(
    { length: endPage - startPage + 1 },
    (_, i) => startPage + i
  );

  this.totalPagesArray = Array.from({ length: this.totalPages }, (_, i) => i + 1);
}

  onPageChange(page: number): void {
    this.currentPage = page;
    this.searchDataService.setPageBoolean(true);
    this.searchDataService.setPage(page);
    this.searchDataService.searchRecipesByTitleContaining();
  }
}
