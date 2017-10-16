package com.aide.chenpan.myaide.bean;

/**
 * Created by Administrator on 2017/9/29.
 */
public class FancyCoverItem {
    private  int drawableID;
    private String name;
    private String data;

    public int getDrawableID() {
        return drawableID;
    }

    public void setDrawableID(int drawableID) {
        this.drawableID = drawableID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public FancyCoverItem( String name, String data,int drawableID) {
        this.drawableID = drawableID;
        this.name = name;
        this.data = data;
    }
}
