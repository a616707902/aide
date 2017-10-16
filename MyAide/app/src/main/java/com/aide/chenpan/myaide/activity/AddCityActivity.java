package com.aide.chenpan.myaide.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aide.chenpan.myaide.R;
import com.aide.chenpan.myaide.adapter.AddCityAdapter;
import com.aide.chenpan.myaide.base.BaseActivity;
import com.aide.chenpan.myaide.bean.County;
import com.aide.chenpan.myaide.common.AideApplication;
import com.aide.chenpan.myaide.common.Constants;
import com.aide.chenpan.myaide.mvp.presenter.AddCityPresenter;
import com.aide.chenpan.myaide.mvp.presenter.BasePresenter;
import com.aide.chenpan.myaide.mvp.view.AddCityView;
import com.aide.chenpan.myaide.other.WeatherDBOperate;
import com.aide.chenpan.myaide.service.LocationService;
import com.aide.chenpan.myaide.utils.NetworkUtils;
import com.aide.chenpan.myaide.utils.SharedPreferencesUtils;
import com.aide.chenpan.myaide.utils.ToastUtil;
import com.aide.chenpan.myaide.utils.WallpaperUtils;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;

import org.litepal.util.LogUtil;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddCityActivity extends BaseActivity implements AddCityView, View.OnClickListener {
    /**
     * Log tag ：AddCityActivity
     */
    private static final String LOG_TAG = "AddCityActivity";

    /**
     * 热门城市标志
     */
    private static final int LEVEL_HOT_CITY = 0;

    /**
     * 省标志
     */
    private static final int LEVEL_PROVINCE = 1;

    /**
     * 市标志
     */
    private static final int LEVEL_CITY = 2;

    /**
     * 县标记
     */
    private static final int LEVEL_COUNTY = 3;

    /**
     * 更多城市和返回按钮的TextView
     */
    private TextView mMoreCityAndReturnBtnTv;

    /**
     * 添加城市列表
     */
    private List<String> mAddCityList;

    /**
     * 添加城市列表适配器
     */
    private AddCityAdapter mAddAddCityAdapter;

    /**
     * 县列表
     */
    private List<County> mCountyList;

    /**
     * 选中的省份
     */
    private String mSelectedProvince;

    /**
     * 选中的市
     */
    private String mSelectedCity;

    /**
     * 当前选中的级别
     */
    private int mCurrentLevel;

    /**
     * 城市列表标题
     */
    private TextView mGvTitle;

    /**
     * 进度对话框
     */
    private Dialog mProgressDialog;

    /**
     * 百度定位服务
     */
    private LocationClient mLocationClient;

    /**
     * 百度定位监听
     */
    private BDLocationListener mBDLocationListener;

    /**
     * 请求MyDialogActivity
     */
    private static final int REQUEST_MY_DIALOG = 1;

    /**
     * 显示热门城市组件LinearLayout
     */
    private LinearLayout mHotCityLlyt;

    /**
     * 清除按钮
     */
    private ImageView mClearBtn;

    /**
     * 当没有匹配到检索的城市时显示的提示内容
     */
    private TextView mNoMatchedCityTv;

    /**
     * 检索城市匹配的列表
     */
    private ListView mSearchCityLv;

    /**
     * 检索城市匹配的列表城市
     */
    private List<SpannableString> mSearchCityList;

    /**
     * 检索城市匹配的列表适配器
     */
    private ArrayAdapter<SpannableString> mSearchCityAdapter;

    /**
     * 输入城市名编辑框
     */
    private EditText mSearchCityEtv;

    /**
     * 全国城市
     */
    private String[] mCountries;

    /**
     * 全国城市代号
     */
    private String[] mWeatherCodes;

    /**
     * 全国城市汉语拼音
     */
    private String[] mCountriesPinyin;

    /**
     * 全国城市汉语拼音缩写
     */
    private String[] mCountriesEn;
    private LocationService locationService;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_city;
    }

    @Override
    protected void initInjector(Bundle savedInstanceState) {
        LinearLayout backGround = (LinearLayout) findViewById(R.id.city_manage_background);
        // 设置页面高斯模糊背景
        WallpaperUtils.setBackgroundBlur(backGround, this);
        initViews();
    }

    private void initViews() {
        // 返回按钮
        ImageView returnBtn = (ImageView) findViewById(R.id.action_return);
        returnBtn.setOnClickListener(this);

        // 更多城市和返回按钮
        LinearLayout moreCityAndReturnBtn = (LinearLayout) findViewById(R.id.more_city_and_return_btn);
        moreCityAndReturnBtn.setOnClickListener(this);
        mMoreCityAndReturnBtnTv = (TextView) findViewById(R.id.more_city_and_return_btn_tv);

        // 编辑框
        mSearchCityEtv = (EditText) findViewById(R.id.search_city_edit_tv);
        mSearchCityEtv.addTextChangedListener(new TextWatcherImpl());

        // 城市视图列表
        mAddCityList = new ArrayList<>();
        mAddAddCityAdapter = new AddCityAdapter(this, mAddCityList);

        mCountyList = new ArrayList<>();

        // 城市列表GridView
        mGvTitle = (TextView) findViewById(R.id.gv_add_city_title);
        GridView addCityGridView = (GridView) findViewById(R.id.gv_add_city);
        addCityGridView.setAdapter(mAddAddCityAdapter);
        addCityGridView.setOnItemClickListener(new AddCityOnItemClickListener());

        // 清除按钮
        mClearBtn = (ImageView) findViewById(R.id.clear_btn);
        mClearBtn.setOnClickListener(this);

        mWeatherCodes = getResources().getStringArray(R.array.city_china_weather_code);
        mCountries = getResources().getStringArray(R.array.city_china);
        mCountriesPinyin = getResources().getStringArray(R.array.city_china_pinyin);
        mCountriesEn = getResources().getStringArray(R.array.city_china_en);

        // 热门城市视图
        mHotCityLlyt = (LinearLayout) findViewById(R.id.city_contents);
        // 无匹配提示
        mNoMatchedCityTv = (TextView) findViewById(R.id.no_matched_city_tv);
        // 查找城市
        mSearchCityLv = (ListView) findViewById(R.id.lv_search_city);
        mSearchCityList = new ArrayList<>();
        mSearchCityAdapter = new ArrayAdapter<>(
                this, R.layout.lv_search_city, mSearchCityList);
        mSearchCityLv.setAdapter(mSearchCityAdapter);
        mSearchCityLv.setOnItemClickListener(new SearchCityOnItemClickListener());
    }

    @Override
    protected void initEventAndData() {
        // 初始化查询热门城市
        queryHotCities();
    }

    @Override
    public void showLoadProgressDialog(String str) {

    }

    @Override
    public void dissDialog() {

    }

    @Override
    public BasePresenter getPresenter() {
        return new AddCityPresenter();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 返回按钮
            case R.id.action_return:
                finish();
                break;
            // 更多城市和返回按钮
            case R.id.more_city_and_return_btn:
                dispatchBackAction(0);
                break;
            // 清除按钮
            case R.id.clear_btn:
                mSearchCityEtv.setText("");
                break;
        }
    }

    /**
     * 显示热门城市
     */
    private void queryHotCities() {
        mAddCityList.clear();
        String[] city = getResources().getStringArray(R.array.city_hot);
        String locationCity=SharedPreferencesUtils.getString(this, Constants.LOCATION_CITY_NAME, null);
        if (!TextUtils.isEmpty(locationCity)){
            city[0]=locationCity;
        }
        Collections.addAll(mAddCityList, city);
        mAddAddCityAdapter.notifyDataSetChanged();
        mGvTitle.setText(R.string.hot_city);
        mCurrentLevel = LEVEL_HOT_CITY;
    }

    class TextWatcherImpl implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            // 输入的城市名
            String cityName = s.toString();
            // 输入内容不为空
            if (!TextUtils.isEmpty(cityName)) {
                // 当list正在滑动中如果清除列表程序会崩溃，因为正在滑动中的项目索引不存在
                mSearchCityList.clear();
                // 隐藏热门城市视图
                mHotCityLlyt.setVisibility(View.GONE);
                // 显示清除按钮
                mClearBtn.setVisibility(View.VISIBLE);

                int length = mCountries.length;
                for (int i = 0; i < length; i++) {
                    // 中文、拼音或拼音简写任意匹配
                    if (mCountries[i].contains(cityName) ||
                            mCountriesPinyin[i].contains(cityName.toLowerCase()) ||
                            mCountriesEn[i].contains(cityName.toLowerCase())) {
                        SpannableString spanString = new SpannableString(mCountries[i]);
                        // 构造一个改变字体颜色的Span
                        @SuppressWarnings("deprecation")
                        ForegroundColorSpan span = new ForegroundColorSpan(getResources().
                                getColor(R.color.white_trans90));

                        // 高亮开始（包括）
                        int start1;
                        // 高亮结束（不包括）
                        int end1;
                        // 当"-"前面的文字包含
                        if (mCountries[i].split("-")[0].contains(cityName) ||
                                mCountriesPinyin[i].split("-")[0].contains(cityName) ||
                                mCountriesEn[i].split("-")[0].contains(cityName)) {
                            start1 = 0;
                            end1 = mCountries[i].indexOf("-") - 1;
                        } else {
                            start1 = mCountries[i].indexOf("-") + 2;
                            end1 = mCountries[i].length();
                        }

                        //将这个Span应用于指定范围的字体
                        spanString.setSpan(span, start1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        mSearchCityList.add(spanString);
                    }
                }

                // 匹配的城市不为空
                if (mSearchCityList.size() != 0) {
                    mSearchCityAdapter.notifyDataSetChanged();
                    // 显示查找城市列表
                    mSearchCityLv.setSelection(0);
                    mSearchCityLv.setVisibility(View.VISIBLE);
                    // 隐藏无匹配城市的提示
                    mNoMatchedCityTv.setVisibility(View.GONE);
                    // 无匹配的城市
                } else {
                    // 隐藏查找城市列表
                    mSearchCityLv.setVisibility(View.GONE);
                    // 显示无匹配城市的提示
                    mNoMatchedCityTv.setVisibility(View.VISIBLE);
                }
                // 输入内容为空
            } else {
                // 显示城市视图
                mHotCityLlyt.setVisibility(View.VISIBLE);
                // 隐藏清除按钮
                mClearBtn.setVisibility(View.GONE);
                // 隐藏查找城市列表
                mSearchCityLv.setVisibility(View.GONE);
                // 隐藏无匹配城市的提示
                mNoMatchedCityTv.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    class AddCityOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 当前选择的城市等级
            switch (mCurrentLevel) {
                case LEVEL_HOT_CITY:
                    if (!NetworkUtils.isNetworkAvailable(AddCityActivity.this)) {
                        ToastUtil.showShortToast(AddCityActivity.this, getString(R.string.internet_error));
                        return;
                    }

                    String cityName = mAddAddCityAdapter.getItem(position);
                    // 当尚未添加此城市
                    if (isCityNoAdd(cityName)) {
                        addCity(cityName);
                    } else {
                        ToastUtil.showShortToast(AddCityActivity.this, getString(
                                R.string.city_already_added, cityName));
                    }
                    break;
                // 省
                case LEVEL_PROVINCE:
                    // 当前选中的省
                    mSelectedProvince = mAddCityList.get(position);
                    // 查询市
                     queryCities();
                    break;
                // 市
                case LEVEL_CITY:
                    // 当前选中的市
                    mSelectedCity = mAddCityList.get(position);
                    // 查询县
                       queryCounties();
                    break;
                // 县
                case LEVEL_COUNTY:
                    if (!NetworkUtils.isNetworkAvailable(AddCityActivity.this)) {
                        ToastUtil.showShortToast(AddCityActivity.this, getString(R.string.internet_error));
                        return;
                    }

                    // 当前选中的县
                    County selectedCounty = mCountyList.get(position);
                    // 当尚未添加此城市
                    if (isCityNoAdd(selectedCounty.getCountyName())) {
                        Intent intent = getIntent();
                        intent.putExtra(Constants.CITY_NAME, selectedCounty.getCountyName());
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    } else {
                        ToastUtil.showShortToast(AddCityActivity.this, getString(
                                R.string.city_already_added, selectedCounty.getCountyName()));
                    }
                    break;
            }
        }
    }

    /**
     * 检查城市是否还没有添加到城市管理表
     *
     * @param cityName 城市名
     * @return 是否没有添加过
     */
    private boolean isCityNoAdd(String cityName) {
        int number = WeatherDBOperate.getInstance().queryCityManage(cityName);
        return number == 0;
    }

    /**
     * 添加城市
     *
     * @param cityName 城市名
     */
    private void addCity(String cityName) {
        switch (cityName) {
            case "定位":
                // 开始定位
                startLocation();
                break;
            default:
                myFinish(cityName);
                break;
        }
    }

    /**
     * 开始定位
     */
    private void startLocation() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            ToastUtil.showShortToast(this, getString(R.string.internet_error));
            return;
        }

        // 初始化定位管理，监听
        initLocationManager();

    }

    /**
     * 初始化定位管理监听
     */
    private void initLocationManager() {

        locationService = ((AideApplication) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();

    }

    /*****
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                locationService.stop();
                locationService.unregisterListener(mListener);
                //定位成功
                if (161 == location.getLocType()) {
                    String cityName = location.getCity();
                    if (cityName != null) {
                        myFinish(cityName);
                    } else {
                        ToastUtil.showShortToast(AddCityActivity.this, getString(R.string.can_not_find_current_location));
                    }

                } else {
                    ToastUtil.showShortToast(AddCityActivity.this, getString(R.string.location_fail));
                }
            } else {
                ToastUtil.showShortToast(AddCityActivity.this, getString(R.string.location_fail));
            }
        }

        public void onConnectHotSpotMessage(String s, int i) {
        }
    };

    /**
     * 结束activity，返回结果到添加城市activity
     *
     * @param weatherName 城市
     */
    private void myFinish(String weatherName) {
        Intent intent = getIntent();
        intent.putExtra(Constants.CITY_NAME, weatherName);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
    /**
     * 查询全国所有的省
     */
    private void queryProvinces() {
        mAddCityList.clear();
        XmlPullParser parser = getResources().getXml(R.xml.city_china);
        try {
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if ("province".equals(parser.getName())) {
                        mAddCityList.add(parser.getAttributeValue(0));
                    }
                }
                eventType = parser.next();
            }
            mAddAddCityAdapter.notifyDataSetChanged();
            mGvTitle.setText(R.string.china);
            mCurrentLevel = LEVEL_PROVINCE;
        } catch (Exception e) {
            LogUtil.d(LOG_TAG, "queryProvinces(): " + e.toString());
        }
    }

    /**
     * 查询全国所有的市
     */
    private void queryCities() {
        mAddCityList.clear();
        XmlPullParser parser = getResources().getXml(R.xml.city_china);
        try {
            int eventType = parser.getEventType();
            String province = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    switch (parser.getName()) {
                        case "province":
                            province = parser.getAttributeValue(0);
                            break;
                        case "city":
                            if (mSelectedProvince.equals(province)) {
                                mAddCityList.add(parser.getAttributeValue(0));
                            }
                            break;
                    }
                }
                eventType = parser.next();
            }
            mAddAddCityAdapter.notifyDataSetChanged();
            mGvTitle.setText(mSelectedProvince);
            mCurrentLevel = LEVEL_CITY;
        } catch (Exception e) {
            LogUtil.d(LOG_TAG, "queryCities(): " + e.toString());
        }
    }

    /**
     * 查询全国所有的县
     */
    private void queryCounties() {
        mAddCityList.clear();
        mCountyList.clear();
        XmlPullParser parser = getResources().getXml(R.xml.city_china);
        try {
            int eventType = parser.getEventType();
            String city = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    switch (parser.getName()) {
                        case "city":
                            city = parser.getAttributeValue(0);
                            break;
                        case "county":
                            if (mSelectedCity.equals(city)) {
                                mAddCityList.add(parser.getAttributeValue(0));

                                County county = new County();
                                county.setCountyName(parser.getAttributeValue(0));
                                county.setWeatherCode(parser.getAttributeValue(2));
                                mCountyList.add(county);
                            }
                            break;
                    }
                }
                eventType = parser.next();
            }
            mAddAddCityAdapter.notifyDataSetChanged();
            mGvTitle.setText(mSelectedCity);
            mCurrentLevel = LEVEL_COUNTY;
        } catch (Exception e) {
            LogUtil.d(LOG_TAG, "queryCounties(): " + e.toString());
        }
    }
    class SearchCityOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!NetworkUtils.isNetworkAvailable(AddCityActivity.this)) {
                ToastUtil.showShortToast(AddCityActivity.this, getString(R.string.internet_error));
                return;
            }

            SpannableString item = mSearchCityAdapter.getItem(position);
            String city = item.toString().split("-")[0].trim();
            LogUtil.d(LOG_TAG, "city：" + city);

            // 当尚未添加此城市
            if (isCityNoAdd(city)) {
               myFinish(city);
            } else {
                ToastUtil.showShortToast(AddCityActivity.this, getString(
                        R.string.city_already_added, city));
            }
        }
    }
    /**
     * 分发返回按钮事件
     *
     * @param type 案件类型：0,更多城市/返回按钮;1,返回键
     */
    private void dispatchBackAction(int type) {
        // 当前城市等级
        switch (mCurrentLevel) {
            // 热门城市
            case LEVEL_HOT_CITY:
                switch (type) {
                    case 0:
                        // 查询省
                        queryProvinces();
                        // 设置为返回按钮
                        mMoreCityAndReturnBtnTv.setText(getString(R.string.back));
                        break;
                    case 1:
                        finish();
                        break;
                }
                break;
            // 省
            case LEVEL_PROVINCE:
                // 查询热门城市
                queryHotCities();
                // 设置为更多城市按钮
                mMoreCityAndReturnBtnTv.setText(getString(R.string.more_city));
                break;
            // 市
            case LEVEL_CITY:
                // 查询省
                queryProvinces();
                break;
            // 县
            case LEVEL_COUNTY:
                // 查询市
                queryCities();
                break;
        }
    }


}
