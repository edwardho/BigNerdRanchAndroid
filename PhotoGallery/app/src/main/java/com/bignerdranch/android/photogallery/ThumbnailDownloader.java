package com.bignerdranch.android.photogallery;

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
    private static final int MESSAGE_DOWNLOAD = 0;

    private boolean mHasQuit = false;
    private Handler mRequestHandler;
    // A thread safe hashmap used to store and retrieve the url associated with a request
    // The request's response can be routed back to the UI element where the downloaded image is placed
    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();
    private Handler mResponseHandler;
    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;

    // Interface for ThumbnailDownloadListener
    public interface ThumbnailDownloadListener<T> {
        void onThumbnailDownload(T target, Bitmap thumbnail);
    }

    // Sets thumbnaildownloadlistener
    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener) {
        mThumbnailDownloadListener = listener;
    }

    // Public constructor
    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    // When the looper is being prepared, set up a new handler
    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    T target = (T) msg.obj;
                    Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }

    // quit
    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }

    // Queue next thumbnail for download
    public void queueThumbnail(T target, String url) {
        Log.i(TAG, "Got a URL: " + url);

        // If url is null, remove the thumbnail from the concurrent hashmap
        if (url == null) {
            mRequestMap.remove(target);
        }
        // Else add the thumbnail and url to the concurrent hashmap
        // Download message and send to the request handler
        else {
            mRequestMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
        }
    }

    // In case of rotation, ThumbnailDownloader may be handing on to invalid PhotoHolders
    // This method clears the queue in order to not pass these ImageViews
    public void clearQueue() {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
        mRequestMap.clear();
    }

    // Handles request
    private void handleRequest(final T target) {
        try{
            final String url = mRequestMap.get(target);

            if (url == null) {
                return;
            }

            byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory
                    .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "Bitmap created");

            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    // if there is no url or if mHasQuit, it may be unsafe to make any callbacks
                    if(mRequestMap.get(target) != url || mHasQuit) {
                        return;
                    }

                    // Remove thumbnail for mRequestMap
                    mRequestMap.remove(target);
                    // Load new thumbnail
                    mThumbnailDownloadListener.onThumbnailDownload(target, bitmap);
                }
            });
        }
        catch (IOException ioe) {
            Log.e(TAG, "Error donwloading image", ioe);
        }
    }
}