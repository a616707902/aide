package com.aide.chenpan.myaide.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/7/31.
 */
public class SearchTripBean extends DataSupport {
    private int resid;
    private String textName;
    private String textAddress;

    public SearchTripBean(int resid, String textname) {
        this.resid = resid;
        this.textName = textname;
    }

    public int getResid() {
        return resid;
    }


    public String getTextname() {
        return textName;
    }

    public String getTextAddress() {
        return textAddress;
    }
}
