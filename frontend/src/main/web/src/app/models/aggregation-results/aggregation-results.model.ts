export interface AggregationResults {
    minCalories: number;
    maxCalories: number;
    minSodium: number;
    maxSodium: number;
    minFat: number;
    maxFat: number;
    minRating: number;
    maxRating: number;
    minProtein: number;
    maxProtein: number;
    [key: string]: number;
}
