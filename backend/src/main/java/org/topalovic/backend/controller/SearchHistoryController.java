package org.topalovic.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.topalovic.backend.model.SearchItem;
import org.topalovic.backend.payload.UserSearchItemsDTO;
import org.topalovic.backend.service.SearchHistoryService;

import java.util.List;

@RestController
@RequestMapping("/api/search-history")
public class SearchHistoryController {
    @Autowired
    private SearchHistoryService searchHistoryService;

    public SearchHistoryController() { }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<SearchItem> findAll() throws Exception {
        return searchHistoryService.findAll();
    }

    @GetMapping("/groupedByUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserSearchItemsDTO>> findAllGroupedByUser() throws Exception {
        List<UserSearchItemsDTO> searchItemsByUser = searchHistoryService.getItemsGroupedByUsers();
        return ResponseEntity.ok(searchItemsByUser);
    }
}
