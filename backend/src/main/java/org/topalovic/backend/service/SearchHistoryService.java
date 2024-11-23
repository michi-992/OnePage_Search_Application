package org.topalovic.backend.service;

import org.topalovic.backend.exceptions.SearchItemListNotFoundException;
import org.topalovic.backend.model.SearchItem;
import org.topalovic.backend.model.UserProfile;
import org.topalovic.backend.payload.UserSearchItemsDTO;

import java.util.List;

public interface SearchHistoryService {
    List<SearchItem> findAll();
    List<SearchItem> findByUserName(String username);
    SearchItem addSearchItem(SearchItem searchItem);
    UserProfile getUser(String username);
    List<UserSearchItemsDTO> getItemsGroupedByUsers();
}
