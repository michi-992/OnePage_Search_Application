package org.topalovic.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.topalovic.backend.model.SearchItem;
import org.topalovic.backend.model.UserProfile;
import org.topalovic.backend.service.SearchItemService;

import java.util.List;

@RestController
@RequestMapping("/api/searchItems")
public class SearchItemController {
    @Autowired
    private SearchItemService searchItemService;

    public SearchItemController() { }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<SearchItem> findAll() throws Exception {
        return searchItemService.findAll();
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasRole('USER')")
    public List<SearchItem> findByUser(@PathVariable("username") String username) throws Exception {
        return searchItemService.findByUserName(username);
    }

    @PostMapping("/user/{username}/add")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SearchItem> addSearchItem(@PathVariable("username") String username, @RequestBody SearchItem searchItem) {
        UserProfile user = searchItemService.getUser(username);
        searchItem.setUser(user);
        SearchItem savedSearchItem = searchItemService.addSearchItem(searchItem);
        return ResponseEntity.ok(savedSearchItem);
    }
}
