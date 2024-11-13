package org.topalovic.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "searchItems")
public class SearchItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String searchTerm;

    private LocalDateTime searchedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile user;

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

    public SearchItem(String searchTerm, UserProfile user) {
        this.searchTerm = searchTerm;
        this.user = user;
    }

    @PrePersist
    protected void onCreate() {
        this.searchedAt = LocalDateTime.now();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) { this.id = id; }

    public String getSearchTerm() {
        return this.searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public LocalDateTime getSearchedAt() {
        return searchedAt;
    }

    public UserProfile getUser() {
        return user;
    }

    public void setUser(UserProfile user) {
        this.user = user;
    }

}
