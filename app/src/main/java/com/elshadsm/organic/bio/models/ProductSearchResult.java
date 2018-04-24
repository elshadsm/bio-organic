package com.elshadsm.organic.bio.models;

public class ProductSearchResult {

    private long id;
    private String name;

    public ProductSearchResult(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ProductSearchResult{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
