package com.elshadsm.organic.bio.models;

import android.os.Parcel;
import android.os.Parcelable;

public class CategoryView implements Parcelable {

    private int iconSource;
    private String title;
    private String category;

    public CategoryView(int iconSource, String title, String category) {
        this.iconSource = iconSource;
        this.title = title;
        this.category = category;
    }

    protected CategoryView(Parcel in) {
        iconSource = in.readInt();
        title = in.readString();
        category = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(iconSource);
        dest.writeString(title);
        dest.writeString(category);
    }

    public static final Creator<CategoryView> CREATOR = new Creator<CategoryView>() {
        @Override
        public CategoryView createFromParcel(Parcel in) {
            return new CategoryView(in);
        }

        @Override
        public CategoryView[] newArray(int size) {
            return new CategoryView[size];
        }
    };

    public int getIconSource() {
        return iconSource;
    }

    public void setIconSource(int iconSource) {
        this.iconSource = iconSource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "CategoryView{" +
                "iconSource=" + iconSource +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
