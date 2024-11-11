package org.topalovic.backend.SearchItemTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.topalovic.backend.exceptions.SearchItemListNotFoundException;
import org.topalovic.backend.model.SearchItem;
import org.topalovic.backend.model.UserProfile;
import org.topalovic.backend.repository.SearchItemRepository;
import org.topalovic.backend.repository.UserRepository;
import org.topalovic.backend.service.SearchItemServiceImpl;
import org.topalovic.backend.service.UserDetailsServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class SearchItemServiceTest {
    @Mock
    SearchItemRepository searchItemRepo;

    @Mock
    UserRepository userRepo;

    @InjectMocks
    SearchItemServiceImpl searchItemService;



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
}
