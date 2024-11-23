package org.topalovic.backend.SearchHistoryTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.topalovic.backend.exceptions.SearchItemListNotFoundException;
import org.topalovic.backend.model.SearchItem;
import org.topalovic.backend.model.UserProfile;
import org.topalovic.backend.payload.UserSearchItemsDTO;
import org.topalovic.backend.repository.SearchHistoryRepository;
import org.topalovic.backend.repository.UserRepository;
import org.topalovic.backend.service.implementations.SearchHistoryServiceImpl;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class SearchHistoryServiceTest {
    @Mock
    SearchHistoryRepository searchItemRepo;

    @Mock
    UserRepository userRepo;

    @InjectMocks
    SearchHistoryServiceImpl searchItemService;



    @Test
    public void getSearchItems() throws Exception {
        List<SearchItem> searchItems = List.of(
                new SearchItem(1L, "searchTerm1"),
                new SearchItem(2L, "searchTerm2")
        );
        List<SearchItem> searchItemsList = searchItems;

        when(searchItemRepo.findAll()).thenReturn(searchItemsList);

        List<SearchItem> foundSearchItems = searchItemRepo.findAll();

        assertThat(foundSearchItems).isEqualTo(searchItemsList);

        verify(searchItemRepo).findAll();
    }

    @Test
    public void getSearchItemsNotFound() {
        when(searchItemRepo.findAll()).thenReturn(Collections.emptyList());

        assertThrows(SearchItemListNotFoundException.class, () -> {
            searchItemService.findAll();
        });

        verify(searchItemRepo).findAll();
    }

    @Test
    public void addSearchItem() {
        SearchItem searchItem = new SearchItem();
        searchItem.setSearchTerm("new search item");

        UserProfile user = new UserProfile();
        user.setUsername("test");
        searchItem.setUser(user);

        when(userRepo.findByUsername("test")).thenReturn(Optional.of(user));
        when(searchItemRepo.save(any(SearchItem.class))).thenReturn(searchItem);

        SearchItem result = searchItemService.addSearchItem(searchItem);

        assertThat(result).isNotNull();
        assertThat(result.getSearchTerm()).isEqualTo("new search item");
        assertThat(result.getUser().getUsername()).isEqualTo("test");

        verify(searchItemRepo).save(searchItem);
    }

    @Test
    public void testGetItemsGroupedByUser() throws SearchItemListNotFoundException {
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

        List<SearchItem> items = Arrays.asList(itemA, itemB, itemC);

        when(searchItemRepo.findAll()).thenReturn(items);

        List<UserSearchItemsDTO> result = searchItemService.getItemsGroupedByUsers();

        assertNotNull(result);
        assertEquals(2, result.size());

        UserSearchItemsDTO userItemsDTO1 = result.stream()
                .filter(dto -> dto.getUser().getId().equals(1L))
                .findFirst()
                .orElse(null);
        assertNotNull(userItemsDTO1);
        assertEquals(user1.getUsername(), userItemsDTO1.getUser().getUsername());
        assertEquals(2, userItemsDTO1.getSearchItems().size());
        assertEquals("item A", userItemsDTO1.getSearchItems().get(0).getSearchTerm());
        assertEquals("item B", userItemsDTO1.getSearchItems().get(1).getSearchTerm());

        UserSearchItemsDTO userItemsDTO2 = result.stream()
                .filter(dto -> dto.getUser().getId().equals(2L))
                .findFirst()
                .orElse(null);
        assertNotNull(userItemsDTO2);
        assertEquals(user2.getUsername(), userItemsDTO2.getUser().getUsername());
        assertEquals(1, userItemsDTO2.getSearchItems().size());
        assertEquals("item C", userItemsDTO2.getSearchItems().get(0).getSearchTerm());
    }
}
