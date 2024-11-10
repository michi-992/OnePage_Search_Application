package org.topalovic.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.topalovic.backend.exceptions.BadRequestException;
import org.topalovic.backend.exceptions.SearchItemListNotFoundException;
import org.topalovic.backend.model.SearchItem;
import org.topalovic.backend.model.UserProfile;
import org.topalovic.backend.repository.SearchItemRepository;
import org.topalovic.backend.repository.UserRepository;

import java.util.List;

@Service
public class SearchItemServiceImpl implements SearchItemService {
    @Autowired
    private SearchItemRepository searchItemRepo;

    @Autowired
    private UserRepository userRepo;

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
    public List<SearchItem> findByUserName(String username) {
        UserProfile user = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return searchItemRepo.findByUser(user);
    }

    @Override
    public SearchItem addSearchItem(SearchItem searchItem, String username) {
        if (searchItem.getSearchTerm() == null || searchItem.getSearchTerm().trim().isEmpty()) {
            throw new BadRequestException("Search term must not be empty or null");
        }

        UserProfile user = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + searchItem.getUser().getUsername()));
        searchItem.setUser(user);

        return searchItemRepo.save(searchItem);
    }
}
