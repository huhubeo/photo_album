package com.weatherbug.photoalbum.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

import com.weatherbug.photoalbum.views.PhotoView;

import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DrawableCache {
    private String TAG = DrawableCache.class.getName();

    private final static String IMAGE_LINK = "https://s3.amazonaws.com/sc.va.util.weatherbug.com/interviewdata/mobilecodingchallenge/";

    public interface OnImageLoadedListener {
        void onLoaded(String key);
        void onFailed(String key);
    }

    private LruCache<String, Drawable> mMemoryCache;
    private Map<String, List<OnImageLoadedListener>> mPendingListeners = new HashMap<>();
    private int mMaxWidth;
    private int mMaxHeight;

    private static DrawableCache mInstance = new DrawableCache();

    public static DrawableCache getInstance() {
        return mInstance;
    }

    private DrawableCache() {
        mMemoryCache = new LruCache<>(100);
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (!keyExists(key)) {
            mMemoryCache.put(key, new BitmapDrawable(bitmap));
        }
    }

    public Drawable getBitmapFromCache(String key) {
        return mMemoryCache.get(key);
    }

    public boolean keyExists(String key) {
        return mMemoryCache.get(key) != null;
    }

    public void loadImageAsync(Context context, String imageUrl, OnImageLoadedListener listener) {
        Log.d(TAG, "loadImageAsync: url=\"" + imageUrl + "\", key=\"" + imageUrl + "\"");
        if (keyExists(imageUrl)) {
            // Already have this image in the cache
            Log.d(TAG, "...getting image from the cache: " + imageUrl);
            if (listener != null) {
                listener.onLoaded(imageUrl);
            }
        } else if (mPendingListeners.containsKey(imageUrl)) {
            // This image is already being loaded
            Log.d(TAG, "...the image is already being loaded: " + imageUrl);
            // Add the listener to the pending list to call it when the loading is done
            addPendingListener(imageUrl, listener);
        } else {
            Log.d(TAG, "...loading the image: " + imageUrl);
            addPendingListener(imageUrl, listener);

            AsyncImageLoader asyncImageLoader = new AsyncImageLoader(context, imageUrl, new OnImageLoadedListener() {
                @Override
                public void onLoaded(String key) {
                    callPendingListeners(key);
                }

                @Override
                public void onFailed(String key) {
                    if (mPendingListeners.containsKey(key)) {
                        mPendingListeners.remove(key);
                    }
                }
            });

            asyncImageLoader.execute(imageUrl);
        }
    }

    public void loadImageAsync(final Context context, final String imageUrl, final View view) {
        Log.d(TAG, "loadImageAsync: url=\"" + imageUrl + "\", key=\"" + imageUrl + "\", view=" + view);
        if (view instanceof PhotoView) {
            ((PhotoView)view).showHideProgressBar(true);
        }
        loadImageAsync(context, IMAGE_LINK + imageUrl, new OnImageLoadedListener() {
            @Override
            public void onLoaded(String key) {
                // At this point the bitmap for this key should be in the cache
                Drawable drawable = getBitmapFromCache(key);
                if (drawable != null) {
                    setDrawable(drawable);
                }
            }

            @Override
            public void onFailed(String key) {
                if (view instanceof PhotoView) {
                    ((PhotoView)view).showHideProgressBar(false);
                }
            }

            private void setDrawable(Drawable drawable) {
                if (drawable != null) {
                    if (view instanceof ImageView) {
                        ((ImageView)view).setImageDrawable(drawable);
                    } else if (view instanceof PhotoView) {
                        ((PhotoView)view).setImage(drawable);
                    }
                }
            }
        });
    }

    public void setMaxSize(int width, int height) {
        mMaxWidth = width;
        mMaxHeight = height;
    }

    private void addPendingListener(String key, OnImageLoadedListener listener) {
        if (TextUtils.isEmpty(key) || listener == null) {
            return;
        }

        List<OnImageLoadedListener> listeners = mPendingListeners.get(key);
        if (listeners == null) {
            listeners = new LinkedList<>();
            mPendingListeners.put(key, listeners);
        }

        listeners.add(listener);
    }

    private void callPendingListeners(String key) {
        if (mPendingListeners.containsKey(key)) {
            for (OnImageLoadedListener listener : mPendingListeners.get(key)) {
                listener.onLoaded(key);
            }
            mPendingListeners.remove(key);
        }
    }

    private class AsyncImageLoader extends AsyncTask<String, Void, Bitmap> {
        Context mContext;

        // Key to access the cached image
        private String mKey;

        private OnImageLoadedListener mListener;

        AsyncImageLoader(Context context, String key, OnImageLoadedListener listener) {
            mContext = context;
            mKey = key;
            mListener = listener;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                String url = params[0];
                if (!StringUtils.isEmpty(url)) {
                    Log.d(TAG, "Image loading: " + mKey);
                    bitmap = getBitmap(url);
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception loading image: " + mKey + " - " + e.getMessage());
                e.printStackTrace();

                if (mListener != null) {
                    mListener.onFailed(mKey);
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            Log.d(TAG, "Image loaded: " + mKey);
            if (bitmap != null) {
                addBitmapToMemoryCache(mKey, bitmap);
            }

            if (mListener != null) {
                if (bitmap != null) {
                    mListener.onLoaded(mKey);
                } else {
                    mListener.onFailed(mKey);
                }
            }
        }

        private InputStream getInputStream(String fileUrl) {
            InputStream is = null;

            try {
                Log.d(TAG, "Try to load from url: " + fileUrl);
                URL aURL = new URL(fileUrl);
                URLConnection conn = aURL.openConnection();
                conn.setUseCaches(true);
                conn.connect();
                is = conn.getInputStream();
            } catch (Exception ex) {
                Log.e(TAG, "Cannot load from url: " + fileUrl + "\n" + ex);
            }

            return is;
        }

        private Bitmap getBitmap(String url) {
            Bitmap scaledBitmap = null;
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(getInputStream(url), null, options);
                int width;
                int height;

                if (options.outWidth > options.outHeight) {
                    width = Math.min(options.outWidth, mMaxWidth);
                    height = (int) (width * (float)options.outHeight / (float)options.outWidth);
                } else {
                    height = Math.min(options.outHeight, mMaxHeight);
                    width = (int) (height * (float)options.outWidth / (float)options.outHeight);
                }

                options.inSampleSize = calculateInSampleSize(options, width, height);
                options.inJustDecodeBounds = false;

                scaledBitmap = BitmapFactory.decodeStream(getInputStream(url), null, options);
            } catch (Exception ex) {
                Log.e(TAG, "Cannot create bitmap from url=" + url);
            }
            return scaledBitmap;
        }

        private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            int height = options.outHeight;
            int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {
                final int halfHeight = height / 2;
                final int halfWidth = width / 2;
                while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                    inSampleSize *= 2;
                }
            }
            return inSampleSize;
        }
    }
}
