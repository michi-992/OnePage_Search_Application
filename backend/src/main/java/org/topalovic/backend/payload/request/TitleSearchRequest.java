package org.topalovic.backend.payload.request;

import org.topalovic.backend.model.SearchItem;
import org.topalovic.backend.model.UserProfile;

public class TitleSearchRequest {
    private SearchItem searchItem;
    private String username;
    private int page = 1;
    private int size = 10;
    private boolean nextPageSearch = false;



    public SearchItem getSearchItem() {
        return searchItem;
    }

    public void setSearchItem(SearchItem searchItem) {
        this.searchItem = searchItem;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean getNextPageSearch() {
        return nextPageSearch;
    }

    public void setNextPageSearch(boolean bool) {
        this.nextPageSearch = bool;
    }
}
