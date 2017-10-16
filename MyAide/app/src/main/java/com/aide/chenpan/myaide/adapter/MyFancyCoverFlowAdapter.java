package com.aide.chenpan.myaide.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.aide.chenpan.myaide.R;
import com.aide.chenpan.myaide.bean.FancyCoverItem;
import com.dalong.francyconverflow.FancyCoverFlow;
import com.dalong.francyconverflow.FancyCoverFlowAdapter;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MyFancyCoverFlowAdapter extends FancyCoverFlowAdapter {
    private Context mContext;

    public List<FancyCoverItem> list;
private String[] names={"金牛座","水瓶座","双鱼座","白羊座","双子座","巨蟹座","狮子座","处女座","天秤座","天蝎座","射手座","摩羯座"};
    private String[] datas={"4/20-5/20","1/20-2/18","2/19-3/20","3/21-4/19","5/21-6/21","6/22-7/22","7/23-8/22","8/23-9/22","9/23-10/23","10/24-11/22","11/23-12/21","12/22-1/19"};
    private int[] pictures={R.drawable.jinniuzuo,R.drawable.shuipingzuo,R.drawable.shuangyuzuo,R.drawable.baiyangzuo,R.drawable.shuangzizuo,R.drawable.juxiezuo,R.drawable.shizizuo,R.drawable.chunvzuo,R.drawable.tianpingzuo,R.drawable.tianxiezuo,R.drawable.sheshouzuo,R.drawable.mojiezuo};

    public MyFancyCoverFlowAdapter(Context context) {
        mContext = context;
        list=new ArrayList<>();
        for (int i=0;i<names.length;i++){
            FancyCoverItem fancyCoverItem=new FancyCoverItem(names[i],datas[i],pictures[i]);
            list.add(fancyCoverItem);
        }

    }

    @Override
    public View getCoverFlowItem(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_fancycoverflow, null);
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            convertView.setLayoutParams(new FancyCoverFlow.LayoutParams(width / 3, FancyCoverFlow.LayoutParams.WRAP_CONTENT));
            holder = new ViewHolder();
            holder.product_img = (CircleImageView) convertView.findViewById(R.id.profile_image);
            holder.product_name = (TextView) convertView.findViewById(R.id.name);
            holder.product_data = (TextView) convertView.findViewById(R.id.data);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final FancyCoverItem item = getItem(position);
        holder.product_name.setText(item.getName());
        holder.product_img.setImageResource(item.getDrawableID());
        holder.product_data.setText(item.getData());
        return convertView;
    }


    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public FancyCoverItem getItem(int i) {
        return list.get(i % list.size());
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    static class ViewHolder {
        CircleImageView product_img;
        TextView product_name;
        TextView product_data;
    }
}
