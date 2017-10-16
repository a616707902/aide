package com.aide.chenpan.myaide.other;

import com.aide.chenpan.myaide.bean.SearchTripBean;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.List;

/**
 * Created by Administrator on 2017/7/31.
 */
public class SearchHistoyDBOperate {
    private static SearchHistoyDBOperate searchHistoyDBOperate;

    private SearchHistoyDBOperate() {
        Connector.getDatabase();
    }
    public synchronized static SearchHistoyDBOperate getInstance() {
        if (searchHistoyDBOperate == null) {
            searchHistoyDBOperate = new SearchHistoyDBOperate();
        }
        return searchHistoyDBOperate;
    }


    /**
     * 将历史搜索实例存储到数据库
     *
     * @param searchTripBean
     * @return 是否存储成功
     */
    public boolean saveSearchHistoy(SearchTripBean searchTripBean) {
        return searchTripBean != null && searchTripBean.saveFast();
    }

    /**
     * 从数据库读取历史记录
     *
     * @return 历史记录数据
     */
    public List<SearchTripBean> loadHistoryData() {
        List<SearchTripBean> searchTripBeanList;
        searchTripBeanList = DataSupport.findAll(SearchTripBean.class);
        return searchTripBeanList;

    }

    /**
     * 将历史记录实例从数据库中删除
     *
     * @param searchTripBean 历史记录实例
     */
    public void deleteCityManage(SearchTripBean searchTripBean) {
        if (searchTripBean != null) {
            searchTripBean.delete();
        }
    }

    /**
     * 删除表中数据
     *
     * @return 件数
     */
    public int deleteAll() {
        return DataSupport.deleteAll(SearchTripBean.class);
    }
}
