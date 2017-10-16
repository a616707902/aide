package com.aide.chenpan.myaide.event;

/**
 * 作者：chenpan
 * 时间：2017/2/9 16:52
 * 邮箱：616707902@qq.com
 * 描述：一个简单的回调类,用于普通的回调
 */

public interface MVPCallBack<T> {
    void succeed(T bean);

    void failed(String message);
}