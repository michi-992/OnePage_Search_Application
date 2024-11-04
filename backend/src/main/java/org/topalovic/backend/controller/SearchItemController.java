package org.topalovic.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.topalovic.backend.model.SearchItem;
import org.topalovic.backend.model.UserProfile;
import org.topalovic.backend.service.SearchItemService;

import java.util.List;

@RestController("/api/")
public class SearchItemController {
    @Autowired
    private SearchItemService searchItemService;

    public SearchItemController() { }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/searchItems")
    public List<SearchItem> findAll() throws Exception {
        return searchItemService.findAll();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/searchItemsByUser")
    public List<SearchItem> findByUser(UserProfile user) throws Exception {
        return searchItemService.findByUser(user);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/searchItems/add")
    public ResponseEntity<SearchItem> addSearchItem(@RequestBody SearchItem searchItem) {
        SearchItem savedSearchItem = searchItemService.addSearchItem(searchItem);
        return ResponseEntity.ok(savedSearchItem);
    }
}
