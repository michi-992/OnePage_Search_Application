package org.topalovic.backend.repository;

import org.opensearch.client.opensearch.core.search.HitsMetadata;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import org.topalovic.backend.model.Recipe;

import java.util.List;

@Repository
public interface RecipeRepository extends ElasticsearchRepository<Recipe, String> {
//    List<Recipe> findByTitleContaining(String title);

//    @Query("{" +
//            "  \"match\": {" +
//            "    \"title\": {" +
//            "      \"query\": \"?0\"," +
//            "      \"operator\": \"and\"," +
//            "      \"fuzziness\": \"AUTO\"" +
//            "    }" +
//            "  }" +
//            "}")
//    SearchHits<Recipe> findByTitleWithFuzzy(String title);

    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title\", \"desc\"], \"type\": \"best_fields\", \"operator\": \"and\", \"fuzziness\": \"AUTO\"}}")
    List<Recipe> findByText(String query);

    @Query("{\"range\": {\"calories\": {\"gte\": \"?0\", \"lte\": \"?1\"}}}")
    List<Recipe> findByCalorieRange(Float minCalories, Float maxCalories, Sort sort);

    @Query("{\"range\": {\"sodium\": {\"gte\": \"?0\", \"lte\": \"?1\"}}}")
    List<Recipe> findBySodiumRange(Float minSodium, Float maxSodium, Sort sort);
}
