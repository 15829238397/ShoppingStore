package com.example.shoppingstore.utils;

import android.widget.StackView;

import com.example.shoppingstore.Bean.ShoppingCart;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by 博 on 2017/7/14.
 */

public class JsonUtil {

    static Gson gson = new Gson() ;

    public static  <T> T fromJson(String json , Type type){
        return gson.fromJson(json , type) ;
    }

    public static String toJSON( Object carts ){
        return gson.toJson(carts) ;
    }


}
