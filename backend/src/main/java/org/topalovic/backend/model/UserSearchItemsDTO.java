package org.topalovic.backend.model;

import java.util.List;

public class UserSearchItemsDTO {
    private UserProfile user;
    private List<SearchItem> searchItems;

    public UserSearchItemsDTO(UserProfile user, List<SearchItem> searchItems) {
        this.user = user;
        this.searchItems = searchItems;
    }

    public UserProfile getUser() {
        return user;
    }

    public void setUser(UserProfile user) {
        this.user = user;
    }

    public List<SearchItem> getSearchItems() {
        return searchItems;
    }

    public void setSearchItems(List<SearchItem> searchItems) {
        this.searchItems = searchItems;
    }
}
