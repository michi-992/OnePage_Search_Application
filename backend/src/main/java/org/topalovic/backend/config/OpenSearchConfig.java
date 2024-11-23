package org.topalovic.backend.config;

import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.spring.boot.autoconfigure.RestClientBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.topalovic.backend.repository.RecipeRepository;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Configuration
@EnableElasticsearchRepositories(basePackageClasses = RecipeRepository.class)
@ComponentScan(basePackageClasses = OpenSearchConfig.class)
public class OpenSearchConfig {

    @Bean
    RestClientBuilderCustomizer customizer() {
        return new RestClientBuilderCustomizer() {
            @Override
            public void customize(HttpAsyncClientBuilder builder) {
                try {
                    builder.setSSLContext(new SSLContextBuilder()
                            .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                            .build());
                } catch (final KeyManagementException | NoSuchAlgorithmException | KeyStoreException ex) {
                    throw new RuntimeException("Failed to initialize SSL Context instance", ex);
                }
            }

            @Override
            public void customize(RestClientBuilder builder) {}
        };
    }

}
