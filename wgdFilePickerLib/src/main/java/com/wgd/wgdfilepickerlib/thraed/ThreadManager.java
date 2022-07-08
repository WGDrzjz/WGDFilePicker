package com.wgd.wgdfilepickerlib.thraed;

import android.os.Handler;
import android.os.Looper;

import com.lzh.easythread.Callback;
import com.lzh.easythread.EasyThread;

public final class ThreadManager {

    private final static EasyThread io;//
    private final static EasyThread cache;
    private final static EasyThread file;

    public static EasyThread getIO () {
        return io;
    }

    public static EasyThread getCache() {
        return cache;
    }

    public static EasyThread getFile() {
        return file;
    }

    static {
        io = EasyThread.Builder.createFixed(6).setName("IO").setPriority(7).setCallback(new DefaultCallback()).build();
        cache = EasyThread.Builder.createCacheable().setName("cache").setCallback(new DefaultCallback()).build();
        file = EasyThread.Builder.createFixed(4).setName("file").setPriority(3).setCallback(new DefaultCallback()).build();
    }

    private static class DefaultCallback implements Callback {

        @Override
        public void onError(String threadName, Throwable t) {

        }

        @Override
        public void onCompleted(String threadName) {

        }

        @Override
        public void onStart(String threadName) {

        }
    }


    public static void onMainHandler(Runnable runnable){
        new Handler(Looper.getMainLooper()).post(runnable);
    }

}
