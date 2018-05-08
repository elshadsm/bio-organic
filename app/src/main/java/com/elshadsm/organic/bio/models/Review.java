package com.elshadsm.organic.bio.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {

    public long id;
    public String date;
    public String review;
    public String fullName;

    public Review() {
    }

    protected Review(Parcel in) {
        id = in.readLong();
        date = in.readString();
        review = in.readString();
        fullName = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(date);
        dest.writeString(review);
        dest.writeString(fullName);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", review='" + review + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
