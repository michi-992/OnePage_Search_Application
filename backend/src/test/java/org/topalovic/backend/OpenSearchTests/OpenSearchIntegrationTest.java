package org.topalovic.backend.OpenSearchTests;

import org.apache.http.HttpHost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensearch.client.Request;
import org.opensearch.client.Response;
import org.opensearch.client.RestClient;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.mapping.Property;
import org.opensearch.client.opensearch.core.BulkRequest;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.bulk.BulkOperation;
import org.opensearch.client.opensearch.core.bulk.IndexOperation;
import org.opensearch.client.opensearch.indices.*;
import org.opensearch.spring.boot.autoconfigure.test.DataOpenSearchTest;
import org.opensearch.testcontainers.OpensearchContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;
import org.topalovic.backend.model.Recipe;
import org.topalovic.backend.repository.RecipeRepository;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DataOpenSearchTest
@Testcontainers(disabledWithoutDocker = true)
@EnableElasticsearchRepositories(basePackageClasses = RecipeRepository.class)
@ContextConfiguration(initializers = OpenSearchIntegrationTest.Initializer.class)
public class OpenSearchIntegrationTest {

    @Container
    static final OpensearchContainer<?> opensearch = new OpensearchContainer<>("opensearchproject/opensearch:2.11.1")
            .withStartupAttempts(5)
            .withStartupTimeout(Duration.ofMinutes(2));

    @Autowired
    private OpenSearchClient client;

    @Autowired
    private RecipeRepository repository;

    private static final String INDEX_NAME = "recipes";

    @BeforeEach
    void setUp() throws IOException {
        try {
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest.Builder().index(INDEX_NAME).build();
            client.indices().delete(deleteIndexRequest);
        } catch (Exception e) {
            System.out.println("Index '" + INDEX_NAME + "' does not exist or failed to delete.");
        }

        Map<String, Property> properties = new HashMap<>();
        properties.put("title", new Property.Builder().text(t -> t).build());
        properties.put("desc", new Property.Builder().text(t -> t).build());
        properties.put("date", new Property.Builder().date(d -> d).build());
        properties.put("categories", new Property.Builder().keyword(k -> k).build());
        properties.put("ingredients", new Property.Builder().keyword(k -> k).build());
        properties.put("directions", new Property.Builder().keyword(k -> k).build());
        properties.put("calories", new Property.Builder().float_(f -> f).build());
        properties.put("fat", new Property.Builder().float_(f -> f).build());
        properties.put("protein", new Property.Builder().float_(f -> f).build());
        properties.put("rating", new Property.Builder().float_(f -> f).build());
        properties.put("sodium", new Property.Builder().float_(f -> f).build());

        CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder()
                .index(INDEX_NAME)
                .mappings(mappingBuilder -> mappingBuilder.properties(properties))
                .build();
        try {
            client.indices().create(createIndexRequest);
        } catch (Exception e) {
            System.err.println("Failed to create index '" + INDEX_NAME + "': " + e.getMessage());
            throw e;
        }
    }


    @Test
    void bulkIndexRecipesFromJson() throws IOException {
        if (client == null) {
            throw new IllegalStateException("OpenSearchClient is not initialized.");
        }


        IndexSettings indexSettings = new IndexSettings.Builder().autoExpandReplicas("0-all").build();
        PutIndicesSettingsRequest putIndicesSettingsRequest = new PutIndicesSettingsRequest.Builder().index(INDEX_NAME).settings(indexSettings).build();
        try {
            client.indices().putSettings(putIndicesSettingsRequest);
        } catch (Exception e) {
            System.err.println("Failed to update settings for index '" + INDEX_NAME + "': " + e.getMessage());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new File("full_format_recipes.json");
        Recipe[] recipes = objectMapper.readValue(jsonFile, Recipe[].class);

        int batchSize = 5000;
        for (int i = 0; i < recipes.length; i += batchSize) {
            BulkRequest.Builder batchBuilder = new BulkRequest.Builder();
            int end = Math.min(i + batchSize, recipes.length);
            for (int j = i; j < end; j++) {
                IndexOperation<Recipe> indexOperation = new IndexOperation.Builder<Recipe>()
                        .index(INDEX_NAME)
                        .id(recipes[j].getId())
                        .document(recipes[j])
                        .build();
                BulkOperation bulkOperation = BulkOperation.of(op -> op.index(indexOperation));
                batchBuilder.operations(bulkOperation);
            }
            BulkRequest batchRequest = batchBuilder.build();
            try {
                client.bulk(batchRequest);
            } catch (Exception e) {
                System.err.println("Failed to index batch " + (i / batchSize + 1) + ": " + e.getMessage());
            }
        }


        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(INDEX_NAME)
                .query(q -> q.matchAll(m -> m))
                .build();

        SearchResponse<Recipe> searchResponse = client.search(searchRequest, Recipe.class);



        assertThat(searchResponse.hits().hits()).isNotEmpty();
        assertThat(searchResponse.hits().hits().getFirst().source().getTitle()).isEqualTo(recipes[0].getTitle());

    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            String opensearchUri = "http://localhost:9200";
            try {
                URL url = new URL(opensearchUri);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int code = connection.getResponseCode();
                if (code == 200) {
                    System.out.println("Local OpenSearch instance is running.");
                } else {
                    System.out.println("Local OpenSearch instance is not running. Starting Testcontainer.");
                    opensearchUri = opensearch.getHttpHostAddress();
                }
            } catch (Exception e) {
                System.out.println("Failed to connect to local OpenSearch instance. Starting Testcontainer.");
                opensearchUri = opensearch.getHttpHostAddress();
            }
            TestPropertyValues.of("opensearch.uris=" + opensearchUri)
                    .applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}