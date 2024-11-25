package org.topalovic.backend.payload.response;

import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.HitsMetadata;
import org.topalovic.backend.model.Recipe;
import org.topalovic.backend.model.SearchItem;

import java.util.List;

public class SearchRequestResponse {
    private List<SearchItem> searchHistory;
    private HitsMetaDataRecipeResponse searchResponse;

    public SearchRequestResponse(List<SearchItem> searchHistory, HitsMetaDataRecipeResponse searchResponse) {
        this.searchHistory = searchHistory;
        this.searchResponse = searchResponse;
    }

    public List<SearchItem> getSearchHistory() {
        return searchHistory;
    }

    public void setSearchHistory(List<SearchItem> searchHistory) {
        this.searchHistory = searchHistory;
    }

    public HitsMetaDataRecipeResponse getSearchResponse() {
        return searchResponse;
    }

    public void setSearchResponse(HitsMetaDataRecipeResponse searchResponse) {
        this.searchResponse = searchResponse;
    }
}
