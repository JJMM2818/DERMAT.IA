package com.example.dermatia;

public class Modelo {
    private String imageUrl;
    public Modelo(){

    }

    public Modelo(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
