// Esta clase se crea en la página 504

package com.bignerdranch.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0; // Se crea en página 509

    private boolean mHasQuit = false;
    private Handler mRequestHandler; // se crea en página 509
    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>(); // se crea en página 509
    private Handler mResponseHandler; // cambios de página 514
    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;// cambios de página 514

    public interface ThumbnailDownloadListener<T>{ // cambios de página 514
        void onThumbnailDownloaded(T target, Bitmap thumbnail); // cambios de página 514
    }

    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener){ // cambios de página 514
        mThumbnailDownloadListener = listener; // cambios de página 514
    }

    public ThumbnailDownloader(Handler responseHandler){ // cambios de página 514
        super(TAG);
        mResponseHandler = responseHandler; // cambios de página 514
    }

    @Override // Cambios de página 511
    protected void onLooperPrepared(){
        mRequestHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if (msg.what == MESSAGE_DOWNLOAD){
                    T target = (T) msg.obj;
                    Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        }; // hasta aquí
    }

    @Override
    public boolean quit(){
        mHasQuit = true;
        return super.quit();
    }

    public void queueThumbnail(T target, String url){
        Log.i(TAG, "Got a URL: " + url);

        if (url == null){ // cambios página 510
            mRequestMap.remove(target);
        }else{
            mRequestMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target)
                    .sendToTarget();
        } // hasta aquí
    }

    public void clearQueue(){ // cambios de página 517
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD); // cambios de página 517
        mRequestMap.clear(); // cambios de página 517
    } // cambios de página 517

    private void handleRequest(final T target){ // Cambios de página 511
        try {
            final String url = mRequestMap.get(target);

            if (url == null){
                return;
            }

            byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory
                    .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "Bitmap created");

            mResponseHandler.post(new Runnable(){  // cambios página 516
                public void run(){ // cambios página 516
                    if (mRequestMap.get(target) != url ||
                    mHasQuit){ // cambios página 516
                        return; // cambios página 516
                    }

                    mRequestMap.remove(target); // cambios página 516
                    mThumbnailDownloadListener.onThumbnailDownloaded(target, bitmap); // cambios página 516
                }
            }); // cambios página 516, hasta aquí
        } catch (IOException ioe){
            Log.e(TAG, "Error downloading image", ioe);
        }
    } // hasta aquí
}
