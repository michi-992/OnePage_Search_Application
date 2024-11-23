package org.topalovic.backend.service.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.topalovic.backend.exceptions.BadRequestException;
import org.topalovic.backend.exceptions.SearchItemListNotFoundException;
import org.topalovic.backend.model.SearchItem;
import org.topalovic.backend.model.UserProfile;
import org.topalovic.backend.payload.UserSearchItemsDTO;
import org.topalovic.backend.repository.SearchHistoryRepository;
import org.topalovic.backend.repository.UserRepository;
import org.topalovic.backend.service.SearchHistoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchHistoryServiceImpl implements SearchHistoryService {
    @Autowired
    private SearchHistoryRepository searchItemRepo;

    @Autowired
    private UserRepository userRepo;

    public SearchHistoryServiceImpl() { }

    public SearchHistoryServiceImpl(SearchHistoryRepository searchItemRepo) {
        this.searchItemRepo = searchItemRepo;
    }

    @Override
    public List<SearchItem> findAll() {
        List<SearchItem> searchItems = searchItemRepo.findAll();

        if (searchItems.isEmpty()) {
            throw new SearchItemListNotFoundException("No history was found.");
        }

        return searchItems;
    }

    @Override
    public List<UserSearchItemsDTO> getItemsGroupedByUsers() {
        List<SearchItem> searchItems = searchItemRepo.findAll();

        if (searchItems.isEmpty()) {
            throw new SearchItemListNotFoundException("No history was found.");
        }

        return searchItems.stream()
                .collect(Collectors.groupingBy(SearchItem::getUser))
                .entrySet()
                .stream()
                .map(entry -> new UserSearchItemsDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SearchItem> findByUserName(String username) {
        UserProfile user = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return searchItemRepo.findByUser(user);
    }

    @Override
    public SearchItem addSearchItem(SearchItem searchItem) {
        if (searchItem.getSearchTerm() == null || searchItem.getSearchTerm().trim().isEmpty()) {
            throw new BadRequestException("Search term must not be empty or null");
        }
        if (searchItem.getUser() == null) {
            throw new BadRequestException("User must not be null");
        } else {
            UserProfile user = userRepo.findByUsername(searchItem.getUser().getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + searchItem.getUser().getUsername()));
        }

        return searchItemRepo.save(searchItem);
    }

    public UserProfile getUser(String username) {
        return userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
}
