package com.aide.chenpan.myaide.utils;

import java.util.List;

/**
 * Created by Administrator on 2017/10/10.
 */
public class ListUtils {
    /**
     * 判断list是否为null
     * @param list
     * @param <D>
     * @return
     */
    public static <D> boolean isEmpty(List<D> list) {
        return list == null || list.isEmpty();
    }

}
