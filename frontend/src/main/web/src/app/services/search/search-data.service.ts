import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http'
import { Observable, Subject, tap } from 'rxjs';
import { SearchItem } from '../../models/search-item/search-item.model'
import { StorageService } from '../storage/storage.service';

const API_URL = 'http://localhost:8080/api/searchItems/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  withCredentials: true
};

@Injectable({
  providedIn: 'root'
})
export class SearchDataService {
  private searchUpdated = new Subject<void>();

  constructor(private http: HttpClient, private storageService: StorageService) { }

  getAllSearchItems(): Observable<any> {
      return this.http.get(API_URL + 'all', httpOptions);
    }

  getSearchItemsByUser(): Observable<any> {
    return this.http.get(API_URL + 'user/' + this.storageService.getUser().username, httpOptions);
  }

  addSearchItem(searchItem: SearchItem): Observable<any> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post(API_URL + 'user/' + this.storageService.getUser().username + '/add', searchItem, httpOptions )
      .pipe(
        tap(() => this.searchUpdated.next())
      );
    }

  getSearchUpdatedListener(): Observable<void> {
      return this.searchUpdated.asObservable();
    }

}
