package org.topalovic.backend.payload.response;

import org.opensearch.client.opensearch.core.search.HitsMetadata;
import org.topalovic.backend.model.Recipe;

import java.util.List;
import java.util.stream.Collectors;

public class HitsMetaDataRecipeResponse {
    private long totalHits;
    private List<Recipe> hits;
    private long time;

    public HitsMetaDataRecipeResponse(HitsMetadata<Recipe> metadata) {
        this.totalHits = metadata.total().value();
        this.hits = metadata.hits().stream()
                .map(hit -> hit.source())
                .collect(Collectors.toList());
    }

    public long getTotalHits() {
        return totalHits;
    }

    public List<Recipe> getHits() {
        return hits;
    }
}
