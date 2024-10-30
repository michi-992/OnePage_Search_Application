package org.topalovic.backend.service;

import org.topalovic.backend.exceptions.SearchItemListNotFoundException;
import org.topalovic.backend.model.SearchItem;

import java.util.List;

public interface SearchItemService {
    List<SearchItem> getSearchItems() throws SearchItemListNotFoundException;
    SearchItem addSearchItem(SearchItem searchItem);
}
