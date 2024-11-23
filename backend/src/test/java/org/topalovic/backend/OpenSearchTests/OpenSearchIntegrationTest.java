package org.topalovic.backend.OpenSearchTests;

import org.junit.jupiter.api.Test;
import org.opensearch.spring.boot.autoconfigure.test.DataOpenSearchTest;
import org.opensearch.testcontainers.OpensearchContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.topalovic.backend.repository.RecipeRepository;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
@DataOpenSearchTest
@EnableElasticsearchRepositories(basePackageClasses = RecipeRepository.class)
@ContextConfiguration(initializers = OpenSearchIntegrationTest.Initializer.class)
public class OpenSearchIntegrationTest {

    @Container
    static final OpensearchContainer<?> opensearch = new OpensearchContainer<>("opensearchproject/opensearch:2.11.1")
            .withStartupAttempts(5)
            .withStartupTimeout(Duration.ofMinutes(2));

    @Test
    void testRecipeRepository(@Autowired RecipeRepository repository) {
        assertThat(repository.findAll()).hasSize(0);
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of("opensearch.uris=" + opensearch.getHttpHostAddress())
                    .applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
