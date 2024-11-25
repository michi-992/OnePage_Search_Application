package org.topalovic.backend.controller;

import org.apache.coyote.Response;
import org.opensearch.client.opensearch.core.search.HitsMetadata;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.topalovic.backend.exceptions.SearchItemListNotFoundException;
import org.topalovic.backend.model.Recipe;
import org.topalovic.backend.model.SearchItem;
import org.topalovic.backend.model.UserProfile;
import org.topalovic.backend.payload.request.CalorieSearchRequest;
import org.topalovic.backend.payload.request.SodiumSearchRequest;
import org.topalovic.backend.payload.request.TitleSearchRequest;
import org.topalovic.backend.payload.response.HitsMetaDataRecipeResponse;
import org.topalovic.backend.payload.response.SearchRequestResponse;
import org.topalovic.backend.service.RecipeService;
import org.topalovic.backend.service.SearchHistoryService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/search-request")
public class SearchRequestController {
    private final RecipeService recipeService;
    private final SearchHistoryService searchHistoryService;

    public SearchRequestController(RecipeService recipeService, SearchHistoryService searchHistoryService) {
        this.recipeService = recipeService;
        this.searchHistoryService = searchHistoryService;
    }

//    @GetMapping(value = "/{username}/search-by-title", produces = MediaType.APPLICATION_JSON_VALUE)
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<SearchRequestResponse> searchByTitle(
//            @RequestParam(value = "title", required = false, defaultValue = "") String title,
//            @PathVariable("username") String username,
//            @RequestBody SearchItem searchItem
//    ) {
//        if(!title.isEmpty()) {
//            // save search item to search history repo
//            UserProfile user = searchHistoryService.getUser(username);
//            searchItem.setUser(user);
//            SearchItem savedSearchItem = searchHistoryService.addSearchItem(searchItem);
//
//            // retrieve search history
//            List<SearchItem> searchHistory = searchHistoryService.findByUserName(username);
//            if (searchHistory.isEmpty()) {
//                throw new SearchItemListNotFoundException("No search history found for user: " + username);
//            }
//
//            // retrieve recipes
//            List<Recipe> recipes = recipeService.findByTitleContaining(title);
//
//
//            SearchRequestResponse response = new SearchRequestResponse(searchHistory, recipes);
//            return ResponseEntity.ok(response);
//        } else {
//            return ResponseEntity.ok(new SearchRequestResponse(Collections.emptyList(), Collections.emptyList()));
//        }
//    }

//    @GetMapping("/search-by-text")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<List<Recipe>> searchRecipes(@RequestParam("search") String query) {
//        List<Recipe> recipes = recipeService.searchByText(query);
//        return ResponseEntity.ok(recipes);
//    }



    @PostMapping("/search-by-text")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SearchRequestResponse> searchRecipesByTitle(
            @RequestBody TitleSearchRequest request
    ) {
        if (request.getSearchItem().getSearchTerm().isEmpty() || request.getUsername() == null || request.getUsername().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        SearchRequestResponse response = recipeService.searchByTitle(request);
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
