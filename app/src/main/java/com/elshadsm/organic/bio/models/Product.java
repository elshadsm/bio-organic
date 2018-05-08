package com.elshadsm.organic.bio.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class Product implements Parcelable {

    public long id;
    public String category;
    public String description;
    public String imageSrc;
    public String insertionDate;
    public String name;
    public float price;
    public float rating;
    public Map<String, Review> reviews = new HashMap<>();
    public String title;

    public Product() {

    }

    protected Product(Parcel in) {
        id = in.readLong();
        category = in.readString();
        description = in.readString();
        imageSrc = in.readString();
        insertionDate = in.readString();
        name = in.readString();
        price = in.readFloat();
        rating = in.readFloat();
        title = in.readString();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            String key = in.readString();
            Review value = in.readParcelable(Review.class.getClassLoader());
            reviews.put(key, value);
        }
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(category);
        dest.writeString(description);
        dest.writeString(imageSrc);
        dest.writeString(insertionDate);
        dest.writeString(name);
        dest.writeFloat(price);
        dest.writeFloat(rating);
        dest.writeString(title);
        dest.writeInt(reviews.size());
        for (Map.Entry<String, Review> entry : reviews.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeParcelable(entry.getValue(), flags);
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    public String getInsertionDate() {
        return insertionDate;
    }

    public void setInsertionDate(String insertionDate) {
        this.insertionDate = insertionDate;
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

    public Map<String, Review> getReviews() {
        return reviews;
    }

    public void setReviews(Map<String, Review> reviews) {
        this.reviews = reviews;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", imageSrc='" + imageSrc + '\'' +
                ", insertionDate='" + insertionDate + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", rating=" + rating +
                ", reviews=" + reviews +
                ", title='" + title + '\'' +
                '}';
    }
}
