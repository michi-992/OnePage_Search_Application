package org.topalovic.backend.SearchItemTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.topalovic.backend.controller.SearchItemController;
import org.topalovic.backend.exceptions.SearchItemListNotFoundException;
import org.topalovic.backend.model.SearchItem;
import org.topalovic.backend.model.UserProfile;
import org.topalovic.backend.model.UserSearchItemsDTO;
import org.topalovic.backend.service.SearchItemService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SearchItemController.class)
public class SearchItemControllerTest {
    @MockBean
    private SearchItemService searchItemService;

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithMockUser
    public void testGetSearchItems() throws Exception {
        List<SearchItem> mockSearchItems = Arrays.asList(
                new SearchItem(1L, "testTerm1"),
                new SearchItem(2L, "testTerm2")
        );

        given(searchItemService.findAll()).willReturn(mockSearchItems);

        String expectedJson = "[{\"id\":1,\"searchTerm\":\"testTerm1\"},{\"id\":2,\"searchTerm\":\"testTerm2\"}]";
        ResultActions result = mockMvc.perform(get("/api/searchItems/all").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

        verify(searchItemService).findAll();
    }

    @Test
    @WithMockUser
    public void testGetSearchItemsNotFound() throws Exception {
        when(searchItemService.findAll()).thenThrow(new SearchItemListNotFoundException());

        ResultActions result = mockMvc.perform(get("/api/searchItems/all"))
                .andExpect(status().isNotFound());

        verify(searchItemService).findAll();
    }

    @Test
    @WithMockUser
    public void testAddSearchItem() throws Exception {
        SearchItem searchItem = new SearchItem();
        searchItem.setSearchTerm("adding new search item");


        String expectedJson = new ObjectMapper().writeValueAsString(searchItem);
        when(searchItemService.addSearchItem(any(SearchItem.class))).thenReturn(searchItem);

        ResultActions result = mockMvc.perform(post("/api/searchItems/user/username/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(searchItem))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

        verify(searchItemService).addSearchItem(any(SearchItem.class));
    }

    @Test
    @WithMockUser
    public void testGetSearchItemsGroupedByUser() throws Exception {
        UserProfile user1 = new UserProfile();
        user1.setId(1L);
        user1.setUsername("user1");

        UserProfile user2 = new UserProfile();
        user2.setId(2L);
        user2.setUsername("user2");

        SearchItem itemA = new SearchItem();
        itemA.setId(1L);
        itemA.setSearchTerm("item A");
        itemA.setUser(user1);

        SearchItem itemB = new SearchItem();
        itemB.setId(2L);
        itemB.setSearchTerm("item B");
        itemB.setUser(user1);

        SearchItem itemC = new SearchItem();
        itemC.setId(3L);
        itemC.setSearchTerm("item C");
        itemC.setUser(user2);

        UserSearchItemsDTO userItemsDTO1 = new UserSearchItemsDTO(user1, Arrays.asList(itemA, itemB));
        UserSearchItemsDTO userItemsDTO2 = new UserSearchItemsDTO(user2, Collections.singletonList(itemC));

        List<UserSearchItemsDTO> userItemsDTOList = Arrays.asList(userItemsDTO1, userItemsDTO2);

        when(searchItemService.getItemsGroupedByUsers()).thenReturn(userItemsDTOList);

        mockMvc.perform(get("/api/searchItems/groupedByUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user.id").value(1L))
                .andExpect(jsonPath("$[0].user.username").value("user1"))
                .andExpect(jsonPath("$[0].searchItems[0].searchTerm").value("item A"))
                .andExpect(jsonPath("$[0].searchItems[1].searchTerm").value("item B"))
                .andExpect(jsonPath("$[1].user.id").value(2L))
                .andExpect(jsonPath("$[1].user.username").value("user2"))
                .andExpect(jsonPath("$[1].searchItems[0].searchTerm").value("item C"));
    }
}