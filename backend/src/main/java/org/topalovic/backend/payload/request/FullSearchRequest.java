package org.topalovic.backend.payload.request;

import org.topalovic.backend.model.SearchItem;

public class FullSearchRequest {
    public SearchItem searchItem;
    public String username;
    public int page = 1;
    public int size = 10;
    public boolean sameSearchTerm = false;
    public String sortField;
    public Float minCalories;
    public Float maxCalories;
    public Float minSodium;
    public Float maxSodium;
    public Float minFat;
    public Float maxFat;
    public Float minRating;
    public Float maxRating;
    public Float minProtein;
    public Float maxProtein;
    public String sortOrder;


    public SearchItem getSearchItem() { return searchItem; }

    public void setSearchItem(SearchItem searchItem) { this.searchItem = searchItem; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public int getPage() { return page; }

    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }

    public void setSize(int size) { this.size = size; }

    public boolean getSameSearchTerm() { return sameSearchTerm; }

    public void setSameSearchTerm(boolean bool) { this.sameSearchTerm = bool; }

    public String getSortField() { return sortField; }

    public void setSortField(String field) { this.sortField = field; }

    public Float getMinCalories() { return minCalories; }

    public void setMinCalories(Float min) { this.minCalories = min; }

    public Float getMaxCalories() { return maxCalories; }

    public void setMaxCalories(Float max) { this.maxCalories = max; }

    public Float getMinSodium() { return minSodium; }

    public void setMinSodium(Float min) { this.minSodium = min; }

    public Float getMaxSodium() { return maxSodium; }

    public void setMaxSodium(Float max) { this.maxSodium = max; }

    public Float getMinFat() { return minFat; }

    public void setMinFat(Float min) { this.minFat = min; }

    public Float getMaxFat() { return maxFat; }

    public void setMaxFat(Float max) { this.maxFat = max; }

    public Float getMinRating() { return minRating; }

    public void setMinRating(Float min) { this.minRating = min; }

    public Float getMaxRating() { return maxRating; }

    public void setMaxRating(Float max) { this.maxRating = max; }

    public Float getMinProtein() { return minProtein; }

    public void setMinProtein(Float min) { this.minProtein = min; }

    public Float getMaxProtein() { return maxProtein; }

    public void setMaxProtein(Float max) { this.maxProtein = max; }

    public String getSortOrder() { return sortOrder; }

    public void setSortOrder(String order) { this.sortOrder = order; }
}
