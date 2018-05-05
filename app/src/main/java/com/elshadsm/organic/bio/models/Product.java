package com.elshadsm.organic.bio.models;

public class Product {

    public long id;
    public String description;
    public Feedback feedback;
    public String imageSrc;
    public String name;
    public float price;
    public float rating;
    public String title;
    public String insertionDate;
    public String category;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInsertionDate() {
        return insertionDate;
    }

    public void setInsertionDate(String insertionDate) {
        this.insertionDate = insertionDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", feedback=" + feedback +
                ", imageSrc='" + imageSrc + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", rating=" + rating +
                ", title='" + title + '\'' +
                ", insertionDate='" + insertionDate + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
