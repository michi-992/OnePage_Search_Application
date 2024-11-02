package org.topalovic.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.topalovic.backend.exceptions.SearchItemListNotFoundException;
import org.topalovic.backend.model.SearchItem;
import org.topalovic.backend.model.UserProfile;
import org.topalovic.backend.repository.SearchItemRepository;

import java.util.List;

@Service
public class SearchItemServiceImpl implements SearchItemService {
    @Autowired
    private SearchItemRepository searchItemRepo;

    public SearchItemServiceImpl() { }

    public SearchItemServiceImpl(SearchItemRepository searchItemRepo) {
        this.searchItemRepo = searchItemRepo;
    }

    @Override
    public List<SearchItem> findAll() throws SearchItemListNotFoundException {
        List<SearchItem> searchItems = searchItemRepo.findAll();
        if (searchItems.isEmpty()) {
            throw new SearchItemListNotFoundException();
        }
        return searchItems;
    }

    @Override
    public List<SearchItem> findByUser(UserProfile user) {
        return searchItemRepo.findByUser(user);
    }

    @Override
    public SearchItem addSearchItem(SearchItem searchItem) {
        return searchItemRepo.save(searchItem);
    }
}
