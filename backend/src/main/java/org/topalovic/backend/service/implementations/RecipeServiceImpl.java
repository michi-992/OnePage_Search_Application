package org.topalovic.backend.service.implementations;

import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.Operator;
import org.opensearch.client.opensearch._types.query_dsl.QueryBuilders;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.opensearch.core.search.HitsMetadata;
import org.opensearch.data.client.osc.NativeQueryBuilder;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.topalovic.backend.exceptions.BadRequestException;
import org.topalovic.backend.exceptions.SearchItemListNotFoundException;
import org.topalovic.backend.model.Recipe;
import org.topalovic.backend.model.SearchItem;
import org.topalovic.backend.model.UserProfile;
import org.topalovic.backend.payload.request.CalorieSearchRequest;
import org.topalovic.backend.payload.request.SodiumSearchRequest;
import org.topalovic.backend.payload.request.TitleSearchRequest;
import org.topalovic.backend.payload.response.HitsMetaDataRecipeResponse;
import org.topalovic.backend.payload.response.SearchRequestResponse;
import org.topalovic.backend.repository.RecipeRepository;
import org.topalovic.backend.repository.SearchHistoryRepository;
import org.topalovic.backend.repository.UserRepository;
import org.topalovic.backend.service.RecipeService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepo;
    private final SearchHistoryRepository searchHistoryRepo;
    private final UserRepository userRepo;
    private final OpenSearchClient openSearchClient;

    public RecipeServiceImpl(RecipeRepository recipeRepo, OpenSearchClient openSearchClient, SearchHistoryRepository searchHistoryRepo, UserRepository userRepo) {
        this.recipeRepo = recipeRepo;
        this.openSearchClient = openSearchClient;
        this.searchHistoryRepo = searchHistoryRepo;
        this.userRepo = userRepo;
    }

//    @Override
//    public SearchResponse<Recipe> findByTitleContaining(String title) {
//        SearchHits<Recipe> hits = recipeRepo.findByTitleWithFuzzy(title);
//        return SearchResponse.;
//    }

    @Override
    public SearchRequestResponse searchByTitle(TitleSearchRequest request) {
        try {
            if (request.getSearchItem().getSearchTerm() == null || request.getSearchItem().getSearchTerm().trim().isEmpty()) {
                throw new BadRequestException("Search term must not be empty or null");
            }
            if (request.getUsername() == null) {
                throw new BadRequestException("User must not be null");
            } else {
                UserProfile user = userRepo.findByUsername(request.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + request.getUsername()));
            }

            UserProfile user = userRepo.findByUsername(request.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + request.getUsername()));
            List<SearchItem> searchHistory = searchHistoryRepo.findByUser(user);

            if(!request.getNextPageSearch()) {

                request.getSearchItem().setUser(user);
                searchHistoryRepo.save(request.getSearchItem());
                searchHistory = searchHistoryRepo.findByUser(user);
                if (searchHistory.isEmpty()) {
                    throw new SearchItemListNotFoundException("No search history found for user: " + request.getUsername());
                }
            }


            SearchRequest searchRequest = SearchRequest.of(req -> req
                    .index("recipes")
                    .query(q -> q
                            .match(m -> m
                                    .field("title")
                                    .query(FieldValue.of(request.getSearchItem().getSearchTerm()))
                                    .operator(Operator.And)
                                    .fuzziness("AUTO")
                            )
                    )
                    .size(request.getSize())
                    .from((request.getPage() - 1) * request.getSize())
            );

            SearchResponse<Recipe> searchResponse = openSearchClient.search(searchRequest, Recipe.class);
            HitsMetaDataRecipeResponse hits = new HitsMetaDataRecipeResponse(searchResponse.hits());

            SearchRequestResponse response = new SearchRequestResponse(searchHistory, hits);
            return response;

        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch recipes from OpenSearch", e);
        }
    }

    @Override
    public HitsMetadata<Recipe> getRecipesByCalorieRange(CalorieSearchRequest request) {
        try {
            SearchRequest searchRequest = SearchRequest.of(req -> req
                    .index("recipes")
                    .query(q -> q
                            .range(r -> r
                                    .field("calories")
                                    .gte(JsonData.of(request.getMinCalories()))
                                    .lte(JsonData.of(request.getMaxCalories()))
                            )
                    )
                    .sort(s -> s
                            .field(f -> f
                                    .field("calories")
                                    .order("asc".equalsIgnoreCase(request.getSortOrder()) ? SortOrder.Asc : SortOrder.Desc)
                            )
                    )
                    .size(request.getSize())
                    .from((request.getPage() - 1) * request.getSize())
            );

            SearchResponse<Recipe> searchResponse = openSearchClient.search(searchRequest, Recipe.class);
            return searchResponse.hits();

        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch recipes from OpenSearch", e);
        }
    }

    @Override
    public HitsMetadata<Recipe> getRecipesBySodiumRange(SodiumSearchRequest request) {
        try {
            SearchRequest searchRequest = SearchRequest.of(req -> req
                    .index("recipes")
                    .query(q -> q
                            .range(r -> r
                                    .field("sodium")
                                    .gte(JsonData.of(request.getMinSodium()))
                                    .lte(JsonData.of(request.getMaxSodium()))
                            )
                    )
                    .sort(s -> s
                            .field(f -> f
                                    .field("sodium")
                                    .order("asc".equalsIgnoreCase(request.getSortOrder()) ? SortOrder.Asc : SortOrder.Desc)
                            )
                    )
                    .size(request.getSize())
                    .from((request.getPage() - 1) * request.getSize())
            );

            SearchResponse<Recipe> searchResponse = openSearchClient.search(searchRequest, Recipe.class);
            return searchResponse.hits();

        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch recipes from OpenSearch", e);
        }
    }
}
