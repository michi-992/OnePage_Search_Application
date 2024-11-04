package org.topalovic.backend.SearchItemTests;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.topalovic.backend.model.SearchItem;
import org.topalovic.backend.model.UserProfile;
import org.topalovic.backend.repository.SearchItemRepository;
import org.topalovic.backend.service.SearchItemService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SearchItemServiceIntegrationTest {

    @Autowired
    private SearchItemService searchItemService;

    @Autowired
    private SearchItemRepository searchItemRepo;

    @Test
    public void addRealSearchItem() {
        SearchItem searchItem = new SearchItem();
        searchItem.setSearchTerm("integration test item");

        SearchItem savedItem = searchItemService.addSearchItem(searchItem);

        assertThat(savedItem).isNotNull();
        assertThat(savedItem.getId()).isNotNull();
        assertThat(savedItem.getSearchTerm()).isEqualTo("integration test item");

        Optional<SearchItem> retrievedItem = searchItemRepo.findById(savedItem.getId());
        assertThat(retrievedItem).isPresent();
        assertThat(retrievedItem.get().getSearchTerm()).isEqualTo("integration test item");
    }
}
