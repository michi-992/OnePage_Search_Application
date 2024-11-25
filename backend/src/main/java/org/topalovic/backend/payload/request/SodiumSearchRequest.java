package org.topalovic.backend.payload.request;

public class SodiumSearchRequest {
    private Float minSodium;
    private Float maxSodium;
    private String sortOrder;
    private int page = 1;
    private int size = 10;



    public Float getMinSodium() {
        return minSodium;
    }

    public void setMinSodium(Float minSodium) {
        this.minSodium = minSodium;
    }

    public Float getMaxSodium() {
        return maxSodium;
    }

    public void setMaxSodium(Float maxSodium) {
        this.maxSodium = maxSodium;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
