package com.example.laundry1app;

public class AllProductsData {
    private String english, url;

    AllProductsData(String english, String url) {
        this.english = english;
        this.url = url;

    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}