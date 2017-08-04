package com.example.shoppingstore.msg;

import java.io.Serializable;

/**
 * Created by 博 on 2017/7/22.
 */

public class BaseRespMsg implements Serializable {

    public final static int STATUS_SUCCESS = 1 ;
    public final static int STATUS_ERROR = 0 ;
    public final static String MSG_SUCCESS = "success" ;

    protected int status = STATUS_SUCCESS ;
    protected String message ;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
