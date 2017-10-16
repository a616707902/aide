package com.aide.chenpan.myaide.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aide.chenpan.myaide.R;


/**
 * Created by lyd10892 on 2016/8/23.
 */

public class NearByHeaderHolder extends RecyclerView.ViewHolder {
    public TextView titleView;
    public ImageView imageView;
    public NearByHeaderHolder(View itemView) {
        super(itemView);
        initView();
    }

    private void initView() {
        imageView=(ImageView) itemView.findViewById(R.id.icon);
        titleView = (TextView) itemView.findViewById(R.id.tv_title);
    }
}
