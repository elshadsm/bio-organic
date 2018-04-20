package com.elshadsm.organic.bio.models;

public class CategoryView {

    private int iconSource;
    private String title;

    public CategoryView(int iconSource, String title) {
        this.iconSource = iconSource;
        this.title = title;
    }

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

    @Override
    public String toString() {
        return "CategoryView{" +
                "iconSource=" + iconSource +
                ", title='" + title + '\'' +
                '}';
    }
}
