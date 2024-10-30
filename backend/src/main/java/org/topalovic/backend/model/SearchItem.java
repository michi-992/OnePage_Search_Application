package org.topalovic.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "searchItems")
public class SearchItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String searchTerm;


    public SearchItem() {
    }

    public SearchItem(Long id, String searchTerm) {
        this.id = id;
        this.searchTerm = searchTerm;
    }

    public SearchItem(String searchterm) {
        this.searchTerm = searchterm;
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
}
