package org.topalovic.backend.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.topalovic.backend.model.Recipe;
import org.topalovic.backend.repository.RecipeRepository;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class RecipeInitializer implements InitializingBean {
    private final RecipeRepository recipeRepo;

    public RecipeInitializer(RecipeRepository recipeRepo) {
        this.recipeRepo = recipeRepo;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<String> categories = Arrays.asList("Dinner", "Chicken", "Healthy");
        List<String> ingredients = Arrays.asList("Chicken breast", "Olive oil", "Garlic", "Salt", "Pepper");
        List<String> directions = Arrays.asList(
                "Preheat oven to 375°F.",
                "Season chicken breast with salt and pepper.",
                "Heat olive oil in a pan and add garlic.",
                "Cook chicken breast in the pan until browned.",
                "Transfer chicken to the oven and bake for 20 minutes."
        );

        recipeRepo.save(new Recipe(
                "1",
                "Baked Chicken Breast",
                "A simple and healthy baked chicken breast recipe.",
                new Date(),
                categories,
                ingredients,
                directions,
                250.0f,
                7.0f,
                35.0f,
                4.5f,
                1200.0f
        ));

        List<String> categories2 = Arrays.asList("Dessert", "Chocolate", "Sweet");
        List<String> ingredients2 = Arrays.asList("Dark chocolate", "Butter", "Sugar", "Eggs", "Flour", "Vanilla extract");
        List<String> directions2 = Arrays.asList(
                "Preheat oven to 350°F.",
                "Melt chocolate and butter together.",
                "Mix in sugar, eggs, and vanilla extract.",
                "Add flour and mix until smooth.",
                "Pour batter into a greased baking dish.",
                "Bake for 25-30 minutes.",
                "Let it cool before serving."
        );

        recipeRepo.save(new Recipe(
                "2",
                "Chocolate Brownies",
                "Rich and delicious chocolate brownies.",
                new Date(),
                categories2,
                ingredients2,
                directions2,
                300.0f,
                15.0f,
                5.0f,
                4.8f,
                500.0f
        ));
    }
}
