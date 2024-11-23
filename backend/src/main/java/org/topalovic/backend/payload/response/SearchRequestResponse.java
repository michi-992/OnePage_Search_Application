package org.topalovic.backend.payload.response;

import org.topalovic.backend.model.Recipe;
import org.topalovic.backend.model.SearchItem;

import java.util.List;

public class SearchRequestResponse {
    private List<SearchItem> searchHistory;
    private List<Recipe> recipes;

    public SearchRequestResponse(List<SearchItem> searchHistory, List<Recipe> recipes) {
        this.searchHistory = searchHistory;
        this.recipes = recipes;
    }

    public List<SearchItem> getSearchHistory() {
        return searchHistory;
    }

    public void setSearchHistory(List<SearchItem> searchHistory) {
        this.searchHistory = searchHistory;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }
}
