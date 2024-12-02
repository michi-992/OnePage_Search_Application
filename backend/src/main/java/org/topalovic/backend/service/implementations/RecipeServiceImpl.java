package org.topalovic.backend.service.implementations;

import jakarta.annotation.PostConstruct;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.aggregations.PercentilesAggregation;
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
import org.topalovic.backend.model.AggregationResults;
import org.topalovic.backend.model.Recipe;
import org.topalovic.backend.model.SearchItem;
import org.topalovic.backend.model.UserProfile;
import org.topalovic.backend.payload.request.CalorieSearchRequest;
import org.topalovic.backend.payload.request.SodiumSearchRequest;
import org.topalovic.backend.payload.request.FullSearchRequest;
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
    private final AggregationResults aggregationResults;

    public RecipeServiceImpl(RecipeRepository recipeRepo, OpenSearchClient openSearchClient, SearchHistoryRepository searchHistoryRepo, UserRepository userRepo, AggregationResults aggregationResults) {
        this.recipeRepo = recipeRepo;
        this.openSearchClient = openSearchClient;
        this.searchHistoryRepo = searchHistoryRepo;
        this.userRepo = userRepo;
        this.aggregationResults = aggregationResults;
    }

    @PostConstruct
    public void init() {
        try {
            fetchAggregations();
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch aggregations on startup", e);
        }
    }

    @Override
    public SearchRequestResponse searchByText(FullSearchRequest request) {
        try {
            validateSearchRequest(request);

            UserProfile user = fetchUserProfile(request.getUsername());
            List<SearchItem> searchHistory = handleSearchHistory(request, user);

            SearchResponse<Recipe> searchResponse = performSearch(request, aggregationResults);

            HitsMetaDataRecipeResponse hits = new HitsMetaDataRecipeResponse(searchResponse.hits());
            return new SearchRequestResponse(searchHistory, hits);

        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch recipes from OpenSearch", e);
        }
    }

    @Override
    public AggregationResults fetchAggregations() throws IOException {
        SearchRequest aggregationRequest = SearchRequest.of(req -> req
                .index("recipes")
                .size(0)
                .aggregations("calories_min", agg -> agg.min(m -> m.field("calories")))
                .aggregations("sodium_min", agg -> agg.min(m -> m.field("sodium")))
                .aggregations("fat_min", agg -> agg.min(m -> m.field("fat")))
                .aggregations("rating_min", agg -> agg.min(m -> m.field("rating")))
                .aggregations("protein_min", agg -> agg.min(m -> m.field("protein")))
                .aggregations("calories_per", agg -> agg.percentiles(p -> p.field("calories").percents(99.0)))
                .aggregations("sodium_per", agg -> agg.percentiles(p -> p.field("sodium").percents(99.0)))
                .aggregations("rating_per", agg -> agg.percentiles(p -> p.field("rating").percents(99.0)))
                .aggregations("fat_per", agg -> agg.percentiles(p -> p.field("fat").percents(99.0)))
                .aggregations("protein_per", agg -> agg.percentiles(p -> p.field("protein").percents(99.0)))
        );

        SearchResponse<Void> aggregationResponse = openSearchClient.search(aggregationRequest, Void.class);

        aggregationResults.setAggregationResults(
                aggregationResponse.aggregations().get("calories_min").min().value(),
                Double.parseDouble(aggregationResponse.aggregations().get("calories_per").tdigestPercentiles().values().keyed().values().iterator().next()),

                aggregationResponse.aggregations().get("sodium_min").min().value(),
                Double.parseDouble(aggregationResponse.aggregations().get("sodium_per").tdigestPercentiles().values().keyed().values().iterator().next()),

                aggregationResponse.aggregations().get("fat_min").min().value(),
                Double.parseDouble(aggregationResponse.aggregations().get("fat_per").tdigestPercentiles().values().keyed().values().iterator().next()),

                aggregationResponse.aggregations().get("rating_min").min().value(),
                Double.parseDouble(aggregationResponse.aggregations().get("rating_per").tdigestPercentiles().values().keyed().values().iterator().next()),

                aggregationResponse.aggregations().get("protein_min").min().value(),
                Double.parseDouble(aggregationResponse.aggregations().get("protein_per").tdigestPercentiles().values().keyed().values().iterator().next())

        );

        return aggregationResults;
    }

    private void validateSearchRequest(FullSearchRequest request) {
        if (request.getSearchItem().getSearchTerm() == null || request.getSearchItem().getSearchTerm().trim().isEmpty()) {
            throw new BadRequestException("Search term must not be empty or null");
        }
        if (request.getUsername() == null) {
            throw new BadRequestException("User must not be null");
        }
    }

    private UserProfile fetchUserProfile(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    private List<SearchItem> handleSearchHistory(FullSearchRequest request, UserProfile user) {
        if (!request.getSameSearchTerm()) {
            request.getSearchItem().setUser(user);
            searchHistoryRepo.save(request.getSearchItem());
        }

        List<SearchItem> searchHistory = searchHistoryRepo.findByUser(user);

        if (searchHistory.isEmpty()) {
            throw new SearchItemListNotFoundException("No search history found for user: " + request.getUsername());
        }
        return searchHistory;
    }


    private SearchResponse<Recipe> performSearch(FullSearchRequest request, AggregationResults aggregations) throws IOException {
        SearchRequest.Builder requestBuilder  = SearchRequest.of(req -> req
                .index("recipes")
                .query(q -> q
                        .bool(b -> {
                            b.must(mq -> mq
                                    .queryString(qs -> qs
                                            .query(request.getSearchItem().getSearchTerm())
                                            .fields("title", "desc")
                                            .defaultOperator(Operator.And)
                                            .fuzziness("AUTO")
                                    )
                            );


                            if (request.getMaxCalories() != null || request.getMinCalories() != null) {
                                b.filter(fq -> fq
                                        .range(r -> r
                                                .field("calories")
                                                .gte(JsonData.of(request.getMinCalories() != null ? request.getMinCalories() : aggregationResults.getMinCalories()))
                                                .lte(JsonData.of(request.getMaxCalories() != null ? request.getMaxCalories() : aggregationResults.getMaxCalories()))
                                        )
                                );
                            }

                            if (request.getMinSodium() != null || request.getMaxSodium() != null) {
                                b.filter(fq -> fq
                                        .range(r -> r
                                                .field("sodium")
                                                .gte(JsonData.of(request.getMinSodium() != null ? request.getMinSodium() : aggregationResults.getMinSodium()))
                                                .lte(JsonData.of(request.getMaxSodium() != null ? request.getMaxSodium() : aggregationResults.getMaxSodium()))
                                        )
                                );
                            }

                            if (request.getMinFat() != null || request.getMaxFat() != null) {
                                b.filter(fq -> fq
                                        .range(r -> r
                                                .field("fat")
                                                .gte(JsonData.of(request.getMinFat() != null ? request.getMinFat() : aggregationResults.getMinFat()))
                                                .lte(JsonData.of(request.getMaxFat() != null ? request.getMaxFat() : aggregationResults.getMaxFat()))
                                        )
                                );
                            }

                            if (request.getMinRating() != null || request.getMaxRating() != null) {
                                b.filter(fq -> fq
                                        .range(r -> r
                                                .field("rating")
                                                .gte(JsonData.of(request.getMinRating() != null ? request.getMinRating() : aggregationResults.getMinRating()))
                                                .lte(JsonData.of(request.getMaxRating() != null ? request.getMaxRating() : aggregationResults.getMaxRating()))
                                        )
                                );
                            }

                            if (request.getMinProtein() != null || request.getMaxProtein() != null) {
                                b.filter(fq -> fq
                                        .range(r -> r
                                                .field("protein")
                                                .gte(JsonData.of(request.getMinProtein() != null ? request.getMinProtein() : aggregationResults.getMinProtein()))
                                                .lte(JsonData.of(request.getMaxProtein() != null ? request.getMaxProtein() : aggregationResults.getMaxProtein()))
                                        )
                                );
                            }
                            return b;
                        })
                )
                .size(request.getSize())
                .from((request.getPage() - 1) * request.getSize())).toBuilder();

        if(request.getSortField() != null && !request.getSortField().trim().isEmpty()) {
            requestBuilder.sort(s -> s
                    .field(f -> f
                            .field(request.getSortField())
                            .order("asc".equalsIgnoreCase(request.getSortOrder()) ? SortOrder.Asc : SortOrder.Desc)
                    )
            );
        }

        SearchRequest searchRequest = requestBuilder.build();

        return openSearchClient.search(searchRequest, Recipe.class);
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
