package com.aide.chenpan.myaide.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aide.chenpan.myaide.R;
import com.aide.chenpan.myaide.event.RxSchedulers;
import com.aide.chenpan.myaide.utils.ListUtils;
import com.aide.chenpan.myaide.utils.LocationUtils;
import com.aide.chenpan.myaide.utils.WebContent;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.bumptech.glide.Glide;
import com.whinc.widget.ratingbar.RatingBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Administrator on 2017/10/11.
 */
public class SearchNearByAdapter extends BaseAdapter {
    private List<PoiDetailResult> mList;
    private Context mContext;
    LayoutInflater layoutInflater;
    LatLng oldLatLng;

    public SearchNearByAdapter(List<PoiDetailResult> mList, Context mContext, LatLng oldLatlng) {
        this.mList = mList;
        this.mContext = mContext;
        this.oldLatLng = oldLatlng;
        layoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return ListUtils.isEmpty(mList) ? 0 : mList.size();
    }

    @Override
    public PoiDetailResult getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(
                    R.layout.search_nearby_reault_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_icon);
            viewHolder.textName = (TextView) convertView.findViewById(R.id.name_tv);
            viewHolder.textDistance = (TextView) convertView.findViewById(R.id.distance_tv);
            viewHolder.textAddress = (TextView) convertView.findViewById(R.id.address_tv);
            viewHolder.textTimes = (TextView) convertView.findViewById(R.id.times);
            viewHolder.mRatingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PoiDetailResult poiInfo = mList.get(position);
        viewHolder.textName.setText(poiInfo.name);
        viewHolder.textDistance.setText(String.valueOf(LocationUtils.getDistance(oldLatLng, poiInfo.location)));
        viewHolder.textAddress.setText(poiInfo.address);
        String urlDetail = (String) viewHolder.imageView.getTag(R.id.imageloader_uri);
        String imageUrl = (String) viewHolder.imageView.getTag(R.id.image_url);
        System.out.print(imageUrl + "ppppppppppp" + position);
        if (TextUtils.isEmpty(imageUrl)) {
            if (!poiInfo.getDetailUrl().equals(urlDetail)) {
                viewHolder.imageView.setImageResource(R.mipmap.ic_launcher);
            }
            getImageByUrlFromDetail(poiInfo.getDetailUrl(), viewHolder.imageView);

        } else {
            Glide.with(mContext)
                    .load(imageUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .crossFade()
                    .into(viewHolder.imageView);
        }

        if (poiInfo.getEnvironmentRating() > 0) {
            viewHolder.mRatingBar.setVisibility(View.VISIBLE);
            viewHolder.mRatingBar.setCount((int) poiInfo.getOverallRating());
        }
        if (!TextUtils.isEmpty(poiInfo.getShopHours())) {
            viewHolder.textTimes.setVisibility(View.VISIBLE);
            viewHolder.textTimes.setText(poiInfo.getShopHours());
        }
        return convertView;
    }

    private void getImageByUrlFromDetail(final String detailUrl, final ImageView imageView) {
        Observable.just(detailUrl).map(new Func1<String, String>() {
            @Override
            public String call(String resString) {
                WebContent webContent = new WebContent();
                String html = null;
                String imageUrl = null;
                try {
                    //访问获取的html
                    Document doc = Jsoup.connect(resString).get();
                    //找到重定向的html地址
                    String url = doc.location();
                    Document docssss = Jsoup.connect(url).get();
                    //获取目标html地址
                    html = docssss.outerHtml();
                    //获取图片地址
                    imageUrl = webContent.getTitle(html);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return imageUrl;
            }
        }).compose(RxSchedulers.schedulersTransformer).subscribe(new Action1<String>() {
            @Override
            public void call(String str) {
                imageView.setTag(R.id.imageloader_uri, detailUrl);
                imageView.setTag(R.id.image_url, str);
                Glide.with(mContext)
                        .load(str)
                        .placeholder(R.mipmap.ic_launcher)
                        .crossFade()
                        .into(imageView);


            }
        });

    }

    public void addMoreData(List<PoiDetailResult> poiInfos) {
        mList.addAll(poiInfos);
        notifyDataSetChanged();
    }

    /**
     * 保存控件实例
     */
    private final class ViewHolder {
        ImageView imageView;
        TextView textName;
        TextView textDistance;
        TextView textAddress;
        TextView textTimes;
        RatingBar mRatingBar;
    }

}
