// Esta clase se crea en la página 504

package com.bignerdranch.photogallery;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0; // Se crea en página 509

    private boolean mHasQuit = false;
    private Handler mRequestHandler; // se crea en página 509
    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>(); // se crea en página 509

    public ThumbnailDownloader(){
        super(TAG);
    }

    @Override
    public boolean quit(){
        mHasQuit = true;
        return super.quit();
    }

    public void queueThumbnail(T target, String url){
        Log.i(TAG, "Got a URL: " + url);
    }
}
