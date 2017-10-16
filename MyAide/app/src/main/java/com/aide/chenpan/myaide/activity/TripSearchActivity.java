package com.aide.chenpan.myaide.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aide.chenpan.myaide.R;
import com.aide.chenpan.myaide.adapter.BaseRecyclerAdapter;
import com.aide.chenpan.myaide.adapter.HistoryAdapter;
import com.aide.chenpan.myaide.adapter.ItemListener;
import com.aide.chenpan.myaide.adapter.RecyclerItemImp;
import com.aide.chenpan.myaide.adapter.SearchHotHolder;
import com.aide.chenpan.myaide.adapter.SearchResultAdapter;
import com.aide.chenpan.myaide.base.BaseActivity;
import com.aide.chenpan.myaide.bean.SearchTripBean;
import com.aide.chenpan.myaide.common.Constants;
import com.aide.chenpan.myaide.mvp.presenter.BasePresenter;
import com.aide.chenpan.myaide.mvp.presenter.TripSearchPresenter;
import com.aide.chenpan.myaide.mvp.view.TripSearchView;
import com.aide.chenpan.myaide.other.SearchHistoyDBOperate;
import com.aide.chenpan.myaide.utils.WallpaperUtils;
import com.baidu.mapapi.search.core.PoiInfo;

import java.util.List;

import butterknife.Bind;

public class TripSearchActivity extends BaseActivity implements TripSearchView, ItemListener<SearchTripBean> {


    public static final int DEFAULT_DURATION = 300;

    private static final AccelerateDecelerateInterpolator DEFAULT_INTERPOLATOR = new AccelerateDecelerateInterpolator();
    @Bind(R.id.action_return)
    ImageView actionReturn;
    @Bind(R.id.action_title)
    TextView actionTitle;
    @Bind(R.id.search_city_edit_tv)
    EditText searchCityEditTv;
    @Bind(R.id.clear_btn)
    ImageView clearBtn;
    @Bind(R.id.search_layout)
    LinearLayout searchLayout;
    @Bind(R.id.trip_search_layout)
    LinearLayout tripSearchLayout;
    @Bind(R.id.search_recy)
    RecyclerView searchRecy;
    @Bind(R.id.histoy_recy)
    RecyclerView histoyRecy;
    @Bind(R.id.clear_tv)
    TextView clearTv;
    @Bind(R.id.list_layout)
    LinearLayout listLayout;
    @Bind(R.id.show_layout)
    RelativeLayout showLayout;
    @Bind(R.id.search_result)
    RecyclerView searchResult;
    @Bind(R.id.search_button)
    TextView searchButton;
    private int originViewLeft;
    private int originViewTop;
    private int originViewWidth;
    private int originViewHeight;
    private int deltaX;
    private int deltaY;
    private float scaleX;
    private float scaleY;
    private HistoryAdapter historyAdapter;
    private BaseRecyclerAdapter mSearchAdapter;
    private Animation translateAnimation;
    private View.OnClickListener onClickListener;
    private SearchResultAdapter searchResultAdapter;
    private int lastVisibleItem;
    int pageNum;
    private String searchString;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_trip_search;
    }

    @Override
    protected void initInjector(Bundle savedInstanceState) {
        // 取出传递过来的originView信息
        extractViewInfoFromBundle(getIntent());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        histoyRecy.setLayoutManager(linearLayoutManager);
        searchResult.setLayoutManager(new LinearLayoutManager(this));
        searchRecy.setLayoutManager(gridLayoutManager);
        searchRecy.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
                outRect.set(10, 10, 10, 10);
            }
        });
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.action_return:
                        onBackPressed();
                        break;
                    case R.id.clear_btn:
                        searchCityEditTv.setText("");
                        break;
                    case R.id.search_button:
                        searchString = searchCityEditTv.getText().toString();
                        SearchHistoyDBOperate.getInstance().saveSearchHistoy(new SearchTripBean(R.drawable.ic_search, searchString));
                        pageNum = 0;
                        searchPOIByText(searchString);
                        break;
                }
            }
        };
        searchCityEditTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchString = s.toString();
                if (!TextUtils.isEmpty(searchString)) {
                    searchButton.setVisibility(View.VISIBLE);
                    clearBtn.setVisibility(View.VISIBLE);
                    pageNum = 0;
                    searchPOIByText(searchString);
                } else {
                    listLayout.setVisibility(View.VISIBLE);
                    searchResult.setVisibility(View.GONE);
                    searchButton.setVisibility(View.GONE);
                    clearBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchResult.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //滑动到最后一项,加载下页的数据
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == searchResultAdapter.getItemCount()) {
                    pageNum++;
                    searchPOIByText(searchString);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
            }
        });
    }

    private void extractViewInfoFromBundle(Intent intent) {
        Bundle bundle = intent.getBundleExtra(Constants.EXTRA_SEARCH_SHAREIMAGE);
        originViewLeft = bundle.getInt("left");
        originViewTop = bundle.getInt("top");
        originViewWidth = bundle.getInt("width");
        originViewHeight = bundle.getInt("height");
    }

    private void onUiReady() {
        searchLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                // remove previous listener
                searchLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                //准备场景
                prepareScene();
                //播放动画
                runEnterAnimation();
                return true;
            }
        });
    }

    private void prepareScene() {
        int[] screenLocation = new int[2];
        searchLayout.getLocationOnScreen(screenLocation);
        //移动到起始view位置
        deltaX = originViewLeft - screenLocation[0];
        deltaY = originViewTop - screenLocation[1];
        searchLayout.setTranslationX(deltaX);
        searchLayout.setTranslationY(deltaY);
        //缩放到起始view大小
        scaleX = (float) originViewWidth / searchLayout.getWidth();
        scaleY = (float) originViewHeight / searchLayout.getHeight();
        searchLayout.setScaleX(scaleX);
        searchLayout.setScaleY(scaleY);
    }

    private void runEnterAnimation() {
        searchLayout.setVisibility(View.VISIBLE);
        //获取图片的颜色，设置背景色
        tripSearchLayout.setBackgroundDrawable(WallpaperUtils.getWallPaperBlurDrawable(this));
        //执行动画
        searchLayout.animate()
                .setDuration(DEFAULT_DURATION)
                .setInterpolator(DEFAULT_INTERPOLATOR)
                .scaleX(1f)
                .scaleY(1f)
                .translationX(0)
                .translationY(0)
                .start();
        showLayout.startAnimation(translateAnimation);

    }

    @Override
    protected void initEventAndData() {
        getSearchListBean();
        actionReturn.setOnClickListener(onClickListener);
        clearBtn.setOnClickListener(onClickListener);
        translateAnimation = AnimationUtils.loadAnimation(this, R.anim.search_list_tranlate_in);
        onUiReady();
    }

    private void getSearchListBean() {
        ((TripSearchPresenter) mPresenter).searchShowRecycleData(this);
    }

    @Override
    public void showLoadProgressDialog(String str) {

    }

    @Override
    public void dissDialog() {

    }

    @Override
    public BasePresenter getPresenter() {
        return new TripSearchPresenter();
    }

    @Override
    public void onBackPressed() {
        runExitAnimation();
    }


    private void runExitAnimation() {
        translateAnimation = AnimationUtils.loadAnimation(this, R.anim.search_list_translate_out);
        searchLayout.animate()
                .setDuration(DEFAULT_DURATION)
                .setInterpolator(DEFAULT_INTERPOLATOR)
                .scaleX(scaleX)
                .scaleY(scaleY)
                .translationX(deltaX)
                .translationY(deltaY)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        overridePendingTransition(0, 0);
                    }
                }).start();
        showLayout.startAnimation(translateAnimation);
    }


    @Override
    public void setHistoryUI(List<SearchTripBean> beans) {

        if (histoyRecy == null)
            return;
        listLayout.setVisibility(View.VISIBLE);
        histoyRecy.setVisibility(View.VISIBLE);
        clearTv.setVisibility(View.VISIBLE);
        historyAdapter = new HistoryAdapter(this, beans, mRecyclerClick);
        histoyRecy.setAdapter(historyAdapter);


    }

    @Override
    public void goneHistoryUI() {
        histoyRecy.setVisibility(View.GONE);
        clearTv.setVisibility(View.GONE);
        listLayout.setVisibility(View.VISIBLE);

    }

    @Override
    public void setSearchUser(List<SearchTripBean> beans) {
        if (searchRecy == null)
            return;
        if (mSearchAdapter == null) {
            mSearchAdapter = new BaseRecyclerAdapter(beans, R.layout.item_search_hot, SearchHotHolder.class, this);
            searchRecy.setAdapter(mSearchAdapter);
        } else {
            if ((mSearchAdapter.getItem(0) == null) && (beans.size() == 0))
                return;
            if ((mSearchAdapter.getItem(0) == null) || (beans.size() == 0) || (!((SearchTripBean) mSearchAdapter.getItem(0))
                    .getTextname().equals(beans.get(0).getTextname())))
                mSearchAdapter.setmDatas(beans);
        }
    }

    @Override
    public void setSearchResult(List<PoiInfo> poiAddrInfos) {
        if (poiAddrInfos == null) {
            return;
        }
        listLayout.setVisibility(View.GONE);
        histoyRecy.setVisibility(View.GONE);
        clearTv.setVisibility(View.GONE);
        searchResult.setVisibility(View.VISIBLE);
        if (pageNum==0){
            searchResultAdapter = new SearchResultAdapter(this, poiAddrInfos, mResultClick);
            searchResult.setAdapter(searchResultAdapter);
        }else{
            searchResultAdapter.addMoreData(poiAddrInfos);
        }


    }

    /**
     * 历史记录点击事件
     */
    private RecyclerItemImp mRecyclerClick = new RecyclerItemImp<SearchTripBean>() {
        @Override
        public void OnClick(int position, SearchTripBean bean) {
//这里执行搜索操作
            String searchText = bean.getTextname();
            //searchPOIByText(searchText);
            searchCityEditTv.setText(searchText);

        }
    };
    /**
     * 搜索结果点击事件
     */
    private RecyclerItemImp mResultClick = new RecyclerItemImp<PoiInfo>() {
        @Override
        public void OnClick(int position, PoiInfo bean) {
            //如果搜索的是poi点，就跳转到显示详情界面
            if (bean.type== PoiInfo.POITYPE.POINT){
                SearchResultShowMapActivity.starResultShowMapActivity(TripSearchActivity.this,bean);
            }else{
                //否则就跳转到公交，地铁显示界面

            }

        }
    };

    /**
     * 常用位置点击事件,将查询到的结果显示在地图上
     *
     * @param view
     * @param postion
     * @param mdata
     */
    @Override
    public void onItemClick(View view, int postion, SearchTripBean mdata) {
        String searchText = mdata.getTextname();
        //这里执行搜索操作
        pageNum=0;
        searchPOIByText(searchText);

    }

    /**
     * 搜索关键字接口
     *
     * @param searchText
     */
    private void searchPOIByText(String searchText) {
        ((TripSearchPresenter) mPresenter).searchCityPOIByString(this, searchText, pageNum);
    }

}
