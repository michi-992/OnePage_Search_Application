import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http'
import { Observable, Subject, tap, delay } from 'rxjs';
import { SearchItem } from '../../models/search-item/search-item.model'
import { StorageService } from '../storage/storage.service';

const API_URL_HISTORY = 'http://localhost:8080/api/searchItems/';
const API_URL_RECIPES = 'http://localhost:8080/api/recipes/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  withCredentials: true
};

@Injectable({
  providedIn: 'root'
})
export class SearchDataService {
  private historyUpdated = new Subject<void>();
  private searchUpdated = new Subject<void>();
  private searchTerm: string = "";

  constructor(private http: HttpClient, private storageService: StorageService) { }

  getAllSearchItems(): Observable<any> {
      return this.http.get(API_URL_HISTORY + 'all', httpOptions);
    }

  getAllSearchItemsGroupedByUser(): Observable<any> {
        return this.http.get(API_URL_HISTORY + 'groupedByUser', httpOptions);
      }


  getSearchItemsByUser(): Observable<any> {
    return this.http.get(API_URL_HISTORY + 'user/' + this.storageService.getUser().username, httpOptions);
  }

  addSearchItem(searchTerm: string): Observable<any> {
  const searchItem: SearchItem = { searchTerm: searchTerm, user: null };
    return this.http.post(API_URL_HISTORY + 'user/' + this.storageService.getUser().username + '/add', searchItem, httpOptions );
    }

  searchRecipesByTitleContaining(title: string): Observable<any> {
    return this.http.get(API_URL_RECIPES + `search-by-title?title=${title}`, httpOptions );
  }

  onSearch(searchTerm: string) {
    this.searchTerm = searchTerm;
    this.addSearchItem(searchTerm).subscribe(
      response => {
        console.log('POST request successful', response);
      },
      error => {
        console.error('Error in POST request', error);
      }
    );
    this.searchRecipesByTitleContaining(searchTerm).subscribe({
      next: (response) => {
        console.log(response);
      },
      error: (err) => console.error(err),
    });

    setTimeout(() => {
      this.searchUpdated.next();
      this.historyUpdated.next();
    }, 500);
  }

  getSearchUpdatedListener(): Observable<void> {
      return this.searchUpdated.asObservable();
    }

  getHistoryUpdatedListener(): Observable<void> {
        return this.historyUpdated.asObservable();
      }

  getSearchTerm(): string {
    return this.searchTerm;
  }

}
