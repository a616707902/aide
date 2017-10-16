package com.aide.chenpan.myaide.adapter;

import android.view.View;

/**
 * Created by Administrator on 2017/3/20.
 */
public interface ItemListener<T> {

     void  onItemClick(View view, int  postion, T mdata);
}
