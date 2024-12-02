package org.topalovic.backend.model;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class AggregationResults {
    private double minCalories;
    private double maxCalories;
    private double minSodium;
    private double maxSodium;
    private double minFat;
    private double maxFat;
    private double minRating;
    private double maxRating;
    private double minProtein;
    private double maxProtein;

    public AggregationResults() {}

    public void setAggregationResults(double minCalories, double maxCalories, double minSodium, double maxSodium, double minFat, double maxFat, double minRating, double maxRating, double minProtein, double maxProtein) {
        this.minCalories = minCalories;
        this.maxCalories = maxCalories;
        this.minSodium = minSodium;
        this.maxSodium = maxSodium;
        this.minFat = minFat;
        this.maxFat = maxFat;
        this.minRating = minRating;
        this.maxRating = maxRating;
        this.minProtein = minProtein;
        this.maxProtein = maxProtein;
    }

    public double getMinCalories() { return minCalories; }
    public double getMaxCalories() { return maxCalories; }
    public double getMinSodium() { return minSodium; }
    public double getMaxSodium() { return maxSodium; }
    public double getMinFat() { return minFat; }
    public double getMaxFat() { return maxFat; }
    public double getMinRating() { return minRating; }
    public double getMaxRating() { return maxRating; }
    public double getMinProtein() { return minProtein; }
    public double getMaxProtein() { return maxProtein; }
}
