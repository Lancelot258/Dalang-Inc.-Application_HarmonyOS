package com.uestc.dalangapp.model;


import ohos.agp.components.Image;

public class Item {

    Integer img_id;
    private Image img;

    public Item() {
    }

    public Item(Integer img_id, Image img) {
        this.img_id = img_id;
    }

    public Integer getImg_id() {
        return img_id;
    }

    public void setImg_id(Integer img_id) {
        this.img_id = img_id;
    }


}