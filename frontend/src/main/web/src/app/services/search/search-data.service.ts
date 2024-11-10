import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http'
import { Observable } from 'rxjs';
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
  constructor(private http: HttpClient, private storageService: StorageService) { }

  getSearchItemsByUser(): Observable<any> {
    console.log(this.storageService.getUser().username);
    return this.http.get(API_URL + 'user/' + this.storageService.getUser().username, httpOptions);
  }

  addSearchItem(searchItem: SearchItem): Observable<any> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post(API_URL + 'user/' + this.storageService.getUser().username + '/add', searchItem, httpOptions );
  }
}
