package org.topalovic.backend.service;


import org.opensearch.client.opensearch.core.search.HitsMetadata;
import org.topalovic.backend.model.AggregationResults;
import org.topalovic.backend.model.Recipe;
import org.topalovic.backend.payload.request.CalorieSearchRequest;
import org.topalovic.backend.payload.request.FullSearchRequest;
import org.topalovic.backend.payload.request.SodiumSearchRequest;
import org.topalovic.backend.payload.response.SearchRequestResponse;

import java.io.IOException;

public interface RecipeService {
    SearchRequestResponse searchByText(FullSearchRequest request);
    AggregationResults fetchAggregations() throws IOException;

    HitsMetadata<Recipe> getRecipesByCalorieRange(CalorieSearchRequest request);
    HitsMetadata<Recipe> getRecipesBySodiumRange(SodiumSearchRequest request);
}

