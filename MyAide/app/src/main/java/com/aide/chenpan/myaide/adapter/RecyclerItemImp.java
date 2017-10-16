package com.aide.chenpan.myaide.adapter;

public interface RecyclerItemImp<T> {
    /**
     * <br/> 方法名称: OnClick
     * <br/> 方法详述: RecyclerView点击事件的回调
     * <br/> 参数: position(点击位置)
     */
    void OnClick(int position,T bean);
}