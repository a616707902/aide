package com.aide.chenpan.myaide.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aide.chenpan.myaide.R;
import com.aide.chenpan.myaide.adapter.ItemListener;
import com.aide.chenpan.myaide.adapter.NearByMenuAdapter;
import com.aide.chenpan.myaide.adapter.SectionedSpanSizeLookup;
import com.aide.chenpan.myaide.base.BaseActivity;
import com.aide.chenpan.myaide.bean.NearByMenuBean;
import com.aide.chenpan.myaide.event.RxSchedulers;
import com.aide.chenpan.myaide.mvp.presenter.BasePresenter;
import com.aide.chenpan.myaide.utils.AssetsUtils;
import com.aide.chenpan.myaide.utils.ToastUtil;
import com.aide.chenpan.myaide.utils.WallpaperUtils;
import com.aide.chenpan.myaide.widget.SideBar;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * 周边菜单详情界面
 */
public class NearByDetailActivity extends BaseActivity implements ItemListener<NearByMenuBean.AllTagsListBean.TagInfoListBean>{

    @Bind(R.id.layout)
    LinearLayout layout;
    @Bind(R.id.action_return)
    ImageView actionReturn;
    @Bind(R.id.action_title)
    TextView actionTitle;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.dialog)
    TextView dialog;
    @Bind(R.id.sidrbar)
    SideBar sidrbar;
    private NearByMenuAdapter nearByMenuAdapter;
    private Drawable mBlurDrawable;
    private GridLayoutManager manager;
    private String localName;
    private LatLng latLng;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_near_by_detail;
    }

    @Override
    protected void initInjector(Bundle savedInstanceState) {
        mBlurDrawable = WallpaperUtils.getWallPaperBlurDrawable(this);
        sidrbar.setTextView(dialog);
        localName=getIntent().getStringExtra("LACALNAME");
       String  title= String.format(getResources().getString(R.string.search_in_nearby),localName);
        actionTitle.setText(title);
        latLng=getIntent().getExtras().getParcelable("LATLNG");
        layout.setBackgroundDrawable(mBlurDrawable);

    }

    @Override
    protected void initEventAndData() {
        Observable.just("json.json").map(new Func1<String, NearByMenuBean>() {
            @Override
            public NearByMenuBean call(String filename) {
                String content = AssetsUtils.readAssetsFile(NearByDetailActivity.this, filename);
                Gson gson = new Gson();
                NearByMenuBean entity = gson.fromJson(content, NearByMenuBean.class);
                return entity;
            }
        }).compose(RxSchedulers.schedulersTransformer).subscribe(new Action1<NearByMenuBean>() {
            @Override
            public void call(NearByMenuBean nearByMenuBean) {
                if (nearByMenuBean != null) {
                    nearByMenuAdapter = new NearByMenuAdapter(NearByDetailActivity.this,NearByDetailActivity.this);
                     manager = new GridLayoutManager(NearByDetailActivity.this,3);
                    //设置header
                    manager.setSpanSizeLookup(new SectionedSpanSizeLookup(nearByMenuAdapter,manager));
                    recyclerView.setLayoutManager(manager);
                    nearByMenuAdapter.setData(nearByMenuBean.getAllTagsList());
                    recyclerView.setAdapter(nearByMenuAdapter);
                } else {
                    ToastUtil.showShortToast(NearByDetailActivity.this, "未能获取到周边信息!");
                }
            }
        });

        // 设置右侧[A-Z]快速导航栏触摸监听
        sidrbar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = nearByMenuAdapter.getPositionForSection(s);
                if (position != -1) {
                    manager.scrollToPositionWithOffset(position, 0);
               //     manager.setStackFromEnd(true);
                   // recyclerView.setSelection(position);
                }
            }
        });
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }


    @OnClick({R.id.action_return, R.id.action_title})
    public void onClick(View view) {
        switch (view.getId()) {
            //返回键
            case R.id.action_return:
                onBackPressed();
                break;
            //跳转到搜索界面
            case R.id.action_title:

                break;
        }
    }

    /**
     * 唯一的跳转接口
     *
     * @param context
     * @param localName 地点名称
     * @param latLng    经纬度
     */
    public static void starNearByActivity(Context context, String localName, LatLng latLng) {
        Intent intent = new Intent(context, NearByDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("LATLNG", latLng);
        intent.putExtras(bundle);
        intent.putExtra("LACALNAME", localName);
        context.startActivity(intent);
    }

    @Override
    public void onItemClick(View view, int postion, NearByMenuBean.AllTagsListBean.TagInfoListBean mdata) {
        ShowNearBySearchResultActivity.starNearBySearchResultActivity(this,localName,latLng,mdata);
    }
}
