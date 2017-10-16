package com.aide.chenpan.myaide.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.aide.chenpan.myaide.R;
import com.aide.chenpan.myaide.bean.SearchTripBean;

/**
 * Created by Administrator on 2017/8/1.
 */
public class SearchHotHolder extends BaseHolder<SearchTripBean> {

    private TextView mTextView;

    public SearchHotHolder(View view) {
        super(view);
        mTextView = (TextView) view.findViewById(R.id.search_hot);
    }

    @Override
    public void init() {
        super.init();
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(v, getPosition(), mData);
            }
        });
    }

    @Override
    public void setData(SearchTripBean mData) {
        super.setData(mData);
        Drawable d =mContext.getResources().getDrawable(mData.getResid());
        d.setBounds(0, 0, 100, 100); //必须设置图片大小，否则不显示
        mTextView.setCompoundDrawables(null , d, null, null);
        mTextView.setText(mData.getTextname());
    }
}
