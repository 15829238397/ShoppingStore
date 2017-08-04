package com.example.shoppingstore.Bean;

/**
 * Created by 博 on 2017/6/25.
 * 定义展示单位所需信息
 */

public class ResyslerViewIndicator {

    private int id ;
    private String title ;
    private ResyslerInformationSliderPart cpOne ;
    private ResyslerInformationSliderPart cpTwo ;
    private ResyslerInformationSliderPart cpThree ;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ResyslerInformationSliderPart getCpOne() {
        return cpOne;
    }

    public void setCpOne(ResyslerInformationSliderPart cpOne) {
        this.cpOne = cpOne;
    }

    public ResyslerInformationSliderPart getCpTwo() {
        return cpTwo;
    }

    public void setCpTwo(ResyslerInformationSliderPart cpTwo) {
        this.cpTwo = cpTwo;
    }

    public ResyslerInformationSliderPart getCpThree() {
        return cpThree;
    }

    public void setCpThree(ResyslerInformationSliderPart cpThree) {
        this.cpThree = cpThree;
    }
}
