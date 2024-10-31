import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http'
import { Observable } from 'rxjs';
import { SearchItem } from '../../models/search-item/search-item.model'

@Injectable({
  providedIn: 'root'
})
export class SearchDataService {
  private apiUrlGetAll = 'http://localhost:8080/searchItems';
  private apiUrlPost = 'http://localhost:8080/searchItems/add';
  constructor(private http: HttpClient) { }

  getSearchItems() {
    return this.http.get<SearchItem[]>(this.apiUrlGetAll);
  }

  addSearchItem(searchItem: SearchItem): Observable<SearchItem> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post<SearchItem>(this.apiUrlPost, searchItem, { headers } );
  }
}
