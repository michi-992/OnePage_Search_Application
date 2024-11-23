package org.topalovic.backend.controller;

import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.topalovic.backend.exceptions.SearchItemListNotFoundException;
import org.topalovic.backend.model.Recipe;
import org.topalovic.backend.model.SearchItem;
import org.topalovic.backend.model.UserProfile;
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

    @GetMapping(value = "/{username}/search-by-title", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SearchRequestResponse> searchByTitle(
            @RequestParam(value = "title", required = false, defaultValue = "") String title,
            @PathVariable("username") String username,
            @RequestBody SearchItem searchItem
    ) {
        if(!title.isEmpty()) {
            // save search item to search history repo
            UserProfile user = searchHistoryService.getUser(username);
            searchItem.setUser(user);
            SearchItem savedSearchItem = searchHistoryService.addSearchItem(searchItem);

            // retrieve search history
            List<SearchItem> searchHistory = searchHistoryService.findByUserName(username);
            if (searchHistory.isEmpty()) {
                throw new SearchItemListNotFoundException("No search history found for user: " + username);
            }

            // retrieve recipes
            List<Recipe> recipes = recipeService.findByTitleContaining(title);


            SearchRequestResponse response = new SearchRequestResponse(searchHistory, recipes);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok(new SearchRequestResponse(Collections.emptyList(), Collections.emptyList()));
        }
    }
}
