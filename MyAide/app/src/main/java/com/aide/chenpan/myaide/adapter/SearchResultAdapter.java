package com.aide.chenpan.myaide.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aide.chenpan.myaide.R;
import com.baidu.mapapi.search.core.PoiInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/8/1.
 */
public class SearchResultAdapter extends RecyclerView.Adapter{
    List<PoiInfo> mList;
    RecyclerItemImp mClick;
    Context mContext;
    public SearchResultAdapter(Context context, List<PoiInfo> list, RecyclerItemImp click) {
        mList = list;
        mClick = click;
        mContext=context;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_history, null);
        return new ViewHolder(view);
    }

    public  void addMoreData( List<PoiInfo> lists){
        this.mList.addAll(lists);
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final PoiInfo poiAddrInfo= mList.get(position);
        ((ViewHolder)holder).mImageGoWhere.setImageResource(R.drawable.gowhere_icon);
        int res=R.drawable.icon_usually;
        switch (poiAddrInfo.type.getInt()){
            case 0:
             break;
            case 1:
                res=R.drawable.icon_bus_station;
                break;
            case 2:
                res=R.drawable.icon_bus_line;
                ((ViewHolder)holder).mImageGoWhere.setVisibility(View.INVISIBLE);
                break;
            case 3:
                res=R.drawable.icon_subway;
                break;
            case 4:
                res=R.drawable.icon_subway_line;
                ((ViewHolder)holder).mImageGoWhere.setVisibility(View.INVISIBLE);
                break;
        }

        ((ViewHolder)holder).mImageHead.setImageResource(res);
        ((ViewHolder)holder).mTitleName.setText(poiAddrInfo.name);
        ((ViewHolder)holder).mTitleAddress.setText(poiAddrInfo.address);
        ((ViewHolder)holder).mItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClick.OnClick(position,poiAddrInfo);
            }
        });
        ((ViewHolder)holder).mImageGoWhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClick.OnClick(position,poiAddrInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout mItemLayout;
        ImageView mImageHead;
        TextView mTitleName;
        TextView mTitleAddress;
        ImageView mImageGoWhere;

        public ViewHolder(View view) {
            super(view);
            mItemLayout=(RelativeLayout) view.findViewById(R.id.layout_item);
            mImageHead= (ImageView) view.findViewById(R.id.img_head);
            mTitleName= (TextView) view.findViewById(R.id.name_tv);
            mTitleAddress= (TextView) view.findViewById(R.id.address_tv);
            mImageGoWhere= (ImageView) view.findViewById(R.id.img_gowhere);
        }
    }
}
