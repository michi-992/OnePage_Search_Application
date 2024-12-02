import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http'
import { Observable, Subject, tap, delay, BehaviorSubject } from 'rxjs';
import { SearchItem } from '../../models/search-item/search-item.model'
import { Recipe } from '../../models/recipe/recipe.model';
import { AggregationResults } from '../../models/aggregation-results/aggregation-results.model';
import { StorageService } from '../storage/storage.service';

const API_URL_HISTORY = 'http://localhost:8080/api/search-history/';
const API_URL_RECIPES = 'http://localhost:8080/api/search-request/';


const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  withCredentials: true
};

@Injectable({
  providedIn: 'root'
})
export class SearchDataService {
  private searchUpdated = new Subject<void>();
  private recipes$ = new BehaviorSubject<Recipe[]>([]);
  private searchHistory$ = new BehaviorSubject<SearchItem[]>([]);
  private totalHits$ = new BehaviorSubject<number>(0);
  private aggregationResults$ = new BehaviorSubject<AggregationResults | null>(null);
  private searchTerm$ = new BehaviorSubject<string>('');

  private searchTerm: string = "";
  private username: string = "";
  private page: number = 1;
  private size: number = 10;
  private sameSearchTerm: boolean = false;
  private minCalories: number | null = null;
  private maxCalories: number | null = null;
  private minSodium: number | null = null;
  private maxSodium: number | null = null;
  private minFat: number | null = null;
  private maxFat: number | null = null;
  private minProtein: number | null = null;
  private maxProtein: number | null = null;
  private minRating: number | null = null;
  private maxRating: number | null = null;
  private sortField: string | null = null;
  private sortOrder: string | null = null;

  constructor(private http: HttpClient, private storageService: StorageService) { 
    this.username = storageService.getUser().username;
    this.fetchAggregationResults();
  }

  getAllSearchItems(): Observable<any> {
      return this.http.get(API_URL_HISTORY + 'all', httpOptions);
    }

  getAllSearchItemsGroupedByUser(): Observable<any> {
        return this.http.get(API_URL_HISTORY + 'groupedByUser', httpOptions);
      }
      
   fetchUserHistory(): void {
    this.http.get<SearchItem[]>(`${API_URL_HISTORY}userHistory/${this.username}`, httpOptions)
      .pipe(
        tap((searchItems: SearchItem[]) => {
          this.searchHistory$.next(searchItems);
        })
      )
      .subscribe();
  }
      

  fetchAggregationResults(): void {
  this.http.get<AggregationResults>(`${API_URL_RECIPES}setup-aggregations`, httpOptions)
    .pipe(
      tap((results: AggregationResults) => {
        this.aggregationResults$.next(results);
      })
    )
      .subscribe();
  }

  getAggregationResults(): Observable<AggregationResults | null> {
    return this.aggregationResults$.asObservable();
  }


  searchRecipesByTitleContaining(): void {
    const requestBody = {
      searchItem: { searchTerm: this.searchTerm, user: null },
      username: this.username,
      page: this.page,
      size: this.size,
      sameSearchTerm: this.sameSearchTerm,
      minCalories: this.minCalories,
      maxCalories: this.maxCalories,
      minSodium: this.minSodium,
      maxSodium: this.maxSodium,
      minFat: this.minFat,
      maxFat: this.maxFat,
      minProtein: this.minProtein,
      maxProtein: this.maxProtein,
      minRating: this.minRating,
      maxRating: this.maxRating,
      sortField: this.sortField,
      sortOrder: this.sortOrder
    };
    console.log(requestBody);

    this.sameSearchTerm = false;

    this.http
      .post(`${API_URL_RECIPES}search-by-text`, requestBody, httpOptions)
      .pipe(
        tap((response: any) => {
          this.totalHits$.next(response.searchResponse.totalHits);
          this.recipes$.next(response.searchResponse.hits || []);
          this.searchHistory$.next(response.searchHistory || []);
        })
      )
      .subscribe();
  }

  getRecipes(): Observable<Recipe[]> {
    return this.recipes$.asObservable();
  }

  getSearchHistory(): Observable<SearchItem[]> {
    return this.searchHistory$.asObservable();
  }

  onSearch(term: string): void {
    this.searchTerm = term;
    this.page = 1;
    this.searchRecipesByTitleContaining();
    this.searchUpdated.next();
    // this.setAllMinMaxToNull();
  }

  getSearchUpdatedListener(): Observable<void> {
    return this.searchUpdated.asObservable();
  }

  getSearchTerm(): string {
    return this.searchTerm;
  }
  setSearchTerm(searchTerm: string): void {
    this.searchTerm = searchTerm;
    this.searchTerm$.next(searchTerm);
  }

  setMinCalories(minValue: number | null): void {
    this.minCalories = minValue;
  }
  setMinSodium(minValue: number | null): void {
    this.minSodium = minValue;
  }
  setMinFat(minValue: number | null): void {
    this.minFat = minValue;
  }
  setMinRating(minValue: number | null): void {
    this.minRating = minValue;
  }
  setMinProtein(minValue: number | null): void {
    this.minProtein = minValue;
  }
  setMaxCalories(maxValue: number | null): void {
    this.maxCalories = maxValue;
  }
  setMaxSodium(maxValue: number | null): void {
    this.maxSodium = maxValue;
  }
  setMaxFat(maxValue: number | null): void {
    this.maxFat = maxValue;
  }
  setMaxRating(maxValue: number | null): void {
    this.maxRating = maxValue;
  }
  setMaxProtein(maxValue: number | null): void {
    this.maxProtein = maxValue;
  }
  setSortOrder(sortOrder: string | null): void {
    this.sortOrder = sortOrder;
  }
  setSortField(sortField: string | null): void {
    console.log(sortField);
    console.log(typeof sortField);
    this.sortField = sortField;
  }

  setAllMinMaxToNull(): void {
    this.setMinCalories(null);
    this.setMinSodium(null);
    this.setMinFat(null);
    this.setMinProtein(null);
    this.setMinRating(null);
    this.setMaxCalories(null);
    this.setMaxSodium(null);
    this.setMaxFat(null);
    this.setMaxProtein(null);
    this.setMaxRating(null);
    this.setSortField(null);
    this.setSortOrder(null);
  }

  getPageBoolean(): boolean {
    return this.sameSearchTerm;
  }

  setPageBoolean(bool: boolean) {
    this.sameSearchTerm = bool;
  }

  getUsername(): string {
    return this.username;
  }

  getPage(): number {
    return this.page;
  }

  getSize(): number {
    return this.size;
  }

  setSize(size: number) {
    this.size = size;
  }

  setPage(page: number): void {
    this.page = page;
  }

  getTotalHitsObservable(): Observable<number> {
    return this.totalHits$.asObservable();
  }

  getSearchTermObservable(): Observable<string> {
    return this.searchTerm$.asObservable();
  }
}