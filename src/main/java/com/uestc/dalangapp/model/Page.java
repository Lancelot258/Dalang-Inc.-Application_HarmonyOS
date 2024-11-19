package com.uestc.dalangapp.model;

public class Page {
    private int image;
    private String name;
    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Page(int image, String name) {
        this.image = image;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Page{" +
                "image=" + image +
                ", name='" + name + '\'' +
                '}';
    }
}
