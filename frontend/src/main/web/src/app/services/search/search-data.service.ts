import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http'
import { Observable, Subject, tap, delay, BehaviorSubject } from 'rxjs';
import { SearchItem } from '../../models/search-item/search-item.model'
import { Recipe } from '../../models/recipe/recipe.model';
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

  private searchTerm: string = "";
  private username: string = "";
  private page: number = 1;
  private size: number = 10;
  private nextPageSearch: boolean = false;

  constructor(private http: HttpClient, private storageService: StorageService) { 
    this.username = storageService.getUser().username;
  }

  getAllSearchItems(): Observable<any> {
      return this.http.get(API_URL_HISTORY + 'all', httpOptions);
    }

  getAllSearchItemsGroupedByUser(): Observable<any> {
        return this.http.get(API_URL_HISTORY + 'groupedByUser', httpOptions);
      }


  // getSearchItemsByUser(): Observable<any> {
  //   return this.http.get(API_URL_HISTORY + 'user/' + this.storageService.getUser().username, httpOptions);
  // }

  // addSearchItem(searchTerm: string): Observable<any> {
  // const searchItem: SearchItem = { searchTerm: searchTerm, user: null };
  //   return this.http.post(API_URL_HISTORY + 'user/' + this.storageService.getUser().username + '/add', searchItem, httpOptions );
  //   }

  searchRecipesByTitleContaining(): void {
    const requestBody = {
      searchItem: { searchTerm: this.searchTerm, user: null },
      username: this.username,
      page: this.page,
      size: this.size,
      nextPageSearch: this.nextPageSearch
    };

    this.nextPageSearch = false;

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
  }

  getSearchUpdatedListener(): Observable<void> {
    return this.searchUpdated.asObservable();
  }

  getSearchTerm(): string {
    return this.searchTerm;
  }

  getPageBoolean(): boolean {
    return this.nextPageSearch;
  }

  setPageBoolean(bool: boolean) {
    this.nextPageSearch = bool;
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

  setPage(page: number): void {
    this.page = page;
  }

  getTotalHitsObservable(): Observable<number> {
    return this.totalHits$.asObservable();
  }
}