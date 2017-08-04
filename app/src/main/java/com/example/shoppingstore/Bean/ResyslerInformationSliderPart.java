package com.example.shoppingstore.Bean;

/**
 * Created by 博 on 2017/7/11.
 */

public class ResyslerInformationSliderPart {

    private int id ;
    private String title ;
    private String imgUrl ;

    public ResyslerInformationSliderPart(String title , String imgUrl) {
        this.title = title ;
        this.imgUrl = imgUrl ;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return this.imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
