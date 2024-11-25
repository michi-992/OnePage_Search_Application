package org.topalovic.backend.service;


import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.HitsMetadata;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.SearchDocumentResponse;
import org.topalovic.backend.model.Recipe;
import org.topalovic.backend.model.SearchItem;
import org.topalovic.backend.payload.request.CalorieSearchRequest;
import org.topalovic.backend.payload.request.SodiumSearchRequest;
import org.topalovic.backend.payload.request.TitleSearchRequest;
import org.topalovic.backend.payload.response.SearchRequestResponse;

import java.io.IOException;
import java.util.List;

public interface RecipeService {
//    public List<Recipe> findByTitleContaining(String title);
    SearchRequestResponse searchByTitle(TitleSearchRequest request);
    HitsMetadata<Recipe> getRecipesByCalorieRange(CalorieSearchRequest request);
    HitsMetadata<Recipe> getRecipesBySodiumRange(SodiumSearchRequest request);
}

