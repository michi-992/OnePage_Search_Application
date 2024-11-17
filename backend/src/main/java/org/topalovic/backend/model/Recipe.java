package org.topalovic.backend.model;

import jakarta.persistence.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

@Document(indexName = "recipes")
public class Recipe {
    @Id
    private String id;

    @Field(type = FieldType.Text, name = "title")
    private String title;

    @Field(type = FieldType.Text, name = "desc")
    private String description;

    @Field(type = FieldType.Date, name = "date")
    private Date date;

    @Field(type = FieldType.Keyword, name = "categories")
    private List<String> categories;

    @Field(type = FieldType.Keyword, name = "ingredients")
    private List<String> ingredients;

    @Field(type = FieldType.Keyword, name = "directions")
    private List<String> directions;

    @Field(type = FieldType.Float, name = "calories")
    private Float calories;

    @Field(type = FieldType.Float, name = "fat")
    private Float fat;

    @Field(type = FieldType.Float, name = "protein")
    private Float protein;

    @Field(type = FieldType.Float, name = "rating")
    private Float rating;

    @Field(type = FieldType.Float, name = "sodium")
    private Float sodium;

    public Recipe() {}

    public Recipe(String id, String title, String description, Date date, List<String> categories, List<String> ingredients, List<String> directions, Float calories, Float fat, Float protein, Float rating, Float sodium) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.categories = categories;
        this.ingredients = ingredients;
        this.directions = directions;
        this.calories = calories;
        this.fat = fat;
        this.protein = protein;
        this.rating = rating;
        this.sodium = sodium;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getDirections() {
        return directions;
    }

    public void setDirections(List<String> directions) {
        this.directions = directions;
    }

    public Float getCalories() {
        return calories;
    }

    public void setCalories(Float calories) {
        this.calories = calories;
    }

    public Float getFat() {
        return fat;
    }

    public void setFat(Float fat) {
        this.fat = fat;
    }

    public Float getProtein() {
        return protein;
    }

    public void setProtein(Float protein) {
        this.protein = protein;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public Float getSodium() {
        return sodium;
    }

    public void setSodium(Float sodium) {
        this.sodium = sodium;
    }
}
