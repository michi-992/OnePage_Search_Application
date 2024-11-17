package org.topalovic.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.topalovic.backend.model.Recipe;
import org.topalovic.backend.service.RecipeService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping(value = "/search-by-title", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Recipe> searchByTitle(@RequestParam(value = "title", required = false, defaultValue = "") String title) {
        if(!title.isEmpty()) {
            return recipeService.findByTitleContaining(title);
        } else {
            return Collections.emptyList();
        }

    }
}
