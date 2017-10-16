package com.aide.chenpan.myaide.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aide.chenpan.myaide.R;
import com.aide.chenpan.myaide.bean.SearchTripBean;

import java.util.List;

/**
 * Created by Administrator on 2017/7/31.
 */
public class HistoryAdapter extends RecyclerView.Adapter {
    private List<SearchTripBean> mList;
    private RecyclerItemImp mClick;
    private Context mContext;

    public HistoryAdapter(Context context, List<SearchTripBean> list, RecyclerItemImp click) {
        mList = list;
        mClick = click;
        mContext=context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_history, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
       SearchTripBean searchTripBean= mList.get(position);
        ((ViewHolder)holder).mImageHead.setImageResource(R.drawable.ic_search);
        ((ViewHolder)holder).mImageGoWhere.setImageResource(R.drawable.gowhere_icon);
        ((ViewHolder)holder).mTitleName.setText(searchTripBean.getTextname());
        ((ViewHolder)holder).mTitleAddress.setText(searchTripBean.getTextAddress());

    }

    @Override
    public int getItemCount() {
         return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageHead;
        TextView mTitleName;
        TextView mTitleAddress;
        ImageView mImageGoWhere;

        public ViewHolder(View view) {
            super(view);
            mImageHead= (ImageView) view.findViewById(R.id.img_head);
            mTitleName= (TextView) view.findViewById(R.id.name_tv);
            mTitleAddress= (TextView) view.findViewById(R.id.address_tv);
            mImageGoWhere= (ImageView) view.findViewById(R.id.img_gowhere);
        }
    }
}
