package org.topalovic.backend.controller;

import org.apache.coyote.Response;
import org.opensearch.client.opensearch.core.search.HitsMetadata;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.topalovic.backend.exceptions.SearchItemListNotFoundException;
import org.topalovic.backend.model.AggregationResults;
import org.topalovic.backend.model.Recipe;
import org.topalovic.backend.model.SearchItem;
import org.topalovic.backend.model.UserProfile;
import org.topalovic.backend.payload.request.CalorieSearchRequest;
import org.topalovic.backend.payload.request.SodiumSearchRequest;
import org.topalovic.backend.payload.request.FullSearchRequest;
import org.topalovic.backend.payload.response.HitsMetaDataRecipeResponse;
import org.topalovic.backend.payload.response.SearchRequestResponse;
import org.topalovic.backend.service.RecipeService;
import org.topalovic.backend.service.SearchHistoryService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/search-request")
public class SearchRequestController {
    private final RecipeService recipeService;
    private final SearchHistoryService searchHistoryService;
    private final AggregationResults aggregationResults;


    public SearchRequestController(RecipeService recipeService, SearchHistoryService searchHistoryService, AggregationResults aggregationResults) {
        this.recipeService = recipeService;
        this.searchHistoryService = searchHistoryService;
        this.aggregationResults = aggregationResults;
    }

    @GetMapping("/setup-aggregations")
    public AggregationResults setUpAggregations() {
        return aggregationResults;
    }

    @PostMapping("/search-by-text")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SearchRequestResponse> searchRecipesByTitle(
            @RequestBody FullSearchRequest request
    ) {
        if (request.getSearchItem().getSearchTerm().isEmpty() || request.getSearchItem().getSearchTerm() == null || request.getUsername() == null  || request.getUsername().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        SearchRequestResponse response = recipeService.searchByText(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/search-by-calories", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<HitsMetaDataRecipeResponse> getRecipesByCalories(
            @RequestBody CalorieSearchRequest request
    ) {
        if (request.getMinCalories() != null && request.getMaxCalories() != null && request.getMinCalories() > request.getMaxCalories()) {
            return ResponseEntity.badRequest().body(null);
        }
        HitsMetadata<Recipe> recipes = recipeService.getRecipesByCalorieRange(request);

        HitsMetaDataRecipeResponse response = new HitsMetaDataRecipeResponse(recipes);

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/search-by-sodium", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<HitsMetaDataRecipeResponse> getRecipesBySodium(
            @RequestBody SodiumSearchRequest request
    ) {
        if (request.getMinSodium() != null && request.getMaxSodium() != null && request.getMinSodium() > request.getMaxSodium()) {
            return ResponseEntity.badRequest().body(null);
        }
        HitsMetadata<Recipe> recipes = recipeService.getRecipesBySodiumRange(request);

        HitsMetaDataRecipeResponse response = new HitsMetaDataRecipeResponse(recipes);

        return ResponseEntity.ok(response);
    }

}
