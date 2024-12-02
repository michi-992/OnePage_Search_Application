package org.topalovic.backend.OpenSearchTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opensearch.client.opensearch.core.search.HitsMetadata;
import org.opensearch.client.opensearch.core.search.TotalHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.topalovic.backend.controller.SearchHistoryController;
import org.topalovic.backend.controller.SearchRequestController;
import org.topalovic.backend.model.AggregationResults;
import org.topalovic.backend.model.Recipe;
import org.topalovic.backend.model.SearchItem;
import org.topalovic.backend.model.UserProfile;
import org.topalovic.backend.payload.UserSearchItemsDTO;
import org.topalovic.backend.payload.request.CalorieSearchRequest;
import org.topalovic.backend.payload.request.FullSearchRequest;
import org.topalovic.backend.payload.request.SodiumSearchRequest;
import org.topalovic.backend.payload.response.HitsMetaDataRecipeResponse;
import org.topalovic.backend.payload.response.SearchRequestResponse;
import org.topalovic.backend.service.RecipeService;
import org.topalovic.backend.service.SearchHistoryService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SearchRequestController.class)
public class SearchRequestControllerTest {
    @MockBean
    private RecipeService recipeService;

    @MockBean
    private SearchHistoryService searchHistoryService;

    @MockBean
    private AggregationResults aggregationResults;

    @Autowired
    MockMvc mockMvc;

    private static final String BASE_URL = "/api/search-request";

    @Test
    @WithMockUser(roles = "USER")
    public void testSearchRecipesByTitle_Success() throws Exception {
        FullSearchRequest request = new FullSearchRequest();
        request.setUsername("testUser");
        request.setSearchItem(new SearchItem());
        request.getSearchItem().setSearchTerm("pizza");

        SearchRequestResponse response = new SearchRequestResponse(null, null);

        when(recipeService.searchByText(any(FullSearchRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/search-request/search-by-text")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testSearchRecipesByTitle_BadRequest() throws Exception {
        FullSearchRequest request = new FullSearchRequest();
        request.setUsername(null);
        request.setSearchItem(new SearchItem());
        request.getSearchItem().setSearchTerm("");

        mockMvc.perform(post("/api/search-request/search-by-text")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetRecipesByCalories_Success() throws Exception {
        CalorieSearchRequest request = new CalorieSearchRequest();
        request.setMinCalories(100f);
        request.setMaxCalories(200f);

        HitsMetadata<Recipe> mockMetadata = mock(HitsMetadata.class);
        TotalHits mockTotalHits = mock(TotalHits.class);
        when(mockTotalHits.value()).thenReturn(1L);
        when(mockMetadata.total()).thenReturn(mockTotalHits);

        when(recipeService.getRecipesByCalorieRange(any(CalorieSearchRequest.class)))
                .thenReturn(mockMetadata);

        mockMvc.perform(post("/api/search-request/search-by-calories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetRecipesByCalories_BadRequest() throws Exception {
        CalorieSearchRequest request = new CalorieSearchRequest();
        request.setMinCalories(200f);
        request.setMaxCalories(100f);

        mockMvc.perform(post("/api/search-request/search-by-calories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetRecipesBySodium_Success() throws Exception {
        SodiumSearchRequest request = new SodiumSearchRequest();
        request.setMinSodium(100f);
        request.setMaxSodium(200f);

        HitsMetadata<Recipe> mockMetadata = mock(HitsMetadata.class);
        TotalHits mockTotalHits = mock(TotalHits.class);
        when(mockTotalHits.value()).thenReturn(1L);
        when(mockMetadata.total()).thenReturn(mockTotalHits);

        when(recipeService.getRecipesBySodiumRange(any(SodiumSearchRequest.class)))
                .thenReturn(mockMetadata);

        mockMvc.perform(post("/api/search-request/search-by-sodium")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetRecipesBySodium_BadRequest() throws Exception {
        SodiumSearchRequest request = new SodiumSearchRequest();
        request.setMinSodium(200f);
        request.setMaxSodium(100f);

        mockMvc.perform(post("/api/search-request/search-by-sodium")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
