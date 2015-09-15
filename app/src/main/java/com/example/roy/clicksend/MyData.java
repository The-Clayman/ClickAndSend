package com.example.roy.clicksend;

import android.app.Application;

import java.util.ArrayList;

import Obj.userData;

/**
 * Created by roy on 4/16/15.
 */
public class MyData extends Application{
    private userData ud;


    public userData getUd(){
        return ud;
    }
    public void setData(userData ud){
        this.ud = ud;
    }

}
