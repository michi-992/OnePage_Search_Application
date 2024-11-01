package org.topalovic.backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "searchItems")
public class SearchItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String searchTerm;

    private LocalDateTime searchedAt;

    public SearchItem() {
    }

    public SearchItem(Long id, String searchTerm) {
        this.id = id;
        this.searchTerm = searchTerm;
        this.searchedAt = LocalDateTime.now();
    }

    public SearchItem(String searchterm) {
        this.searchTerm = searchterm;
        this.searchedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.searchedAt = LocalDateTime.now();
    }

    public Long getId() {
        return this.id;
    }

    public String getSearchTerm() {
        return this.searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public LocalDateTime getSearchedAt() {
        return searchedAt;
    }

}
