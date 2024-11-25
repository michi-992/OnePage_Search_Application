package org.topalovic.backend.payload.request;

public class CalorieSearchRequest {
    private Float minCalories;
    private Float maxCalories;
    private String sortOrder;
    private int page = 1;
    private int size = 10;



    public Float getMinCalories() {
        return minCalories;
    }

    public void setMinCalories(Float minCalories) {
        this.minCalories = minCalories;
    }

    public Float getMaxCalories() {
        return maxCalories;
    }

    public void setMaxCalories(Float maxCalories) {
        this.maxCalories = maxCalories;
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
