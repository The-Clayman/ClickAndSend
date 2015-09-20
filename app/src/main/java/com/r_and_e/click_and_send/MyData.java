package com.r_and_e.click_and_send;

import android.app.Application;

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
