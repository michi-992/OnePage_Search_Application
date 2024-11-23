package org.topalovic.backend.service.implementations;

import org.springframework.stereotype.Service;
import org.topalovic.backend.model.Recipe;
import org.topalovic.backend.repository.RecipeRepository;
import org.topalovic.backend.service.RecipeService;

import java.io.IOException;
import java.util.List;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepo;

    public RecipeServiceImpl(RecipeRepository recipeRepo) {
        this.recipeRepo = recipeRepo;
    }

    @Override
    public List<Recipe> findByTitleContaining(String title) {
        return recipeRepo.findByTitleContaining(title);
    }
}
