package org.topalovic.backend.service;


import org.topalovic.backend.model.Recipe;

import java.util.List;

public interface RecipeService {
    public List<Recipe> findByTitleContaining(String title);
}
