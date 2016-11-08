package com.example.djurus.gag7;

/**
 * Created by djurus on 11/6/16.
 */

public class Gag {
    private String title;
    private String imgSrc;

    public Gag(String title, String imgSrc){
        this.title= title;
        this.imgSrc= imgSrc;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public String getTitle() {
        return title;
    }
}
