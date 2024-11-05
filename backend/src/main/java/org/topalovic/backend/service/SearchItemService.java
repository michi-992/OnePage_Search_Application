package org.topalovic.backend.service;

import org.topalovic.backend.exceptions.SearchItemListNotFoundException;
import org.topalovic.backend.model.SearchItem;
import org.topalovic.backend.model.UserProfile;

import java.util.List;

public interface SearchItemService {
    List<SearchItem> findAll() throws SearchItemListNotFoundException;
    List<SearchItem> findByUserName(String username) throws SearchItemListNotFoundException;
    SearchItem addSearchItem(SearchItem searchItem);
}
