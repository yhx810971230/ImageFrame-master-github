package com.mrwang.imageframe;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import java.util.ArrayList;
import java.util.List;

/**
 * 后台单线程的handler
 * User: chengwangyong(chengwangyong@blinnnk.com)
 * Date: 2017/3/16
 * Time: 下午4:39
 */
public class WorkHandler extends android.os.HandlerThread {
    private Handler workHandler = null;

    private volatile List<WorkMessageProxy> messageProxyList;

    Looper looper = null;

    public WorkHandler() {
        super("WorkHandler", Process.THREAD_PRIORITY_BACKGROUND);
        start();
        looper = getLooper();
        workHandler = new Handler(looper) {
            @Override
            public void handleMessage(Message msg) {
                //根据bugly这里偶现一个空指针
                try {
                    if (null != messageProxyList && messageProxyList.size() > 0) {
                        for (WorkMessageProxy workMessageProxy : messageProxyList) {
                            if (null != workMessageProxy) {
                                workMessageProxy.handleMessage(msg);
                            }
                        }
                    }
                }
                catch (Exception e){

                }
            }
        };
    }

    public void quitLooper() {
        if (null != looper) {
            looper.quit();
        }
    }

    public void post(Runnable run) {
        workHandler.post(run);
    }

    public void postAtFrontOfQueue(Runnable runnable) {
        workHandler.postAtFrontOfQueue(runnable);
    }

    public void postDelayed(Runnable runnable, long delay) {
        workHandler.postDelayed(runnable, delay);
    }

    public void postAtTime(Runnable runnable, long time) {
        workHandler.postAtTime(runnable, time);
    }

    public void addMessageProxy(WorkMessageProxy proxy) {
        initMessageProxyList();
        messageProxyList.add(proxy);
    }

    public void removeMessageProxy(WorkMessageProxy proxy) {
        initMessageProxyList();
        messageProxyList.remove(proxy);
    }

    private void initMessageProxyList() {
        if (messageProxyList == null) {
            messageProxyList = new ArrayList<>();
        }
    }

    public Handler getHandler() {
        return workHandler;
    }

    public interface WorkMessageProxy {
        void handleMessage(Message msg);
    }

}
