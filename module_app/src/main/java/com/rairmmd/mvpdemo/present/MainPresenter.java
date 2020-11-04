package com.rairmmd.mvpdemo.present;

import com.rairmmd.andmvp.base.BasePresent;
import com.rairmmd.mvpdemo.MainActivity;
import com.socks.library.KLog;

/**
 * @author Rair
 * @date 2018/7/5
 * <p>
 * desc:
 */
public class MainPresenter extends BasePresent<MainActivity> {
    public void printHello(){
        KLog.v("hello world");
    }
}
