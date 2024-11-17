package org.topalovic.backend.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import org.topalovic.backend.model.Recipe;

import java.util.List;

@Repository
public interface RecipeRepository extends ElasticsearchRepository<Recipe, String> {
    List<Recipe> findByTitleContaining(String title);
}
