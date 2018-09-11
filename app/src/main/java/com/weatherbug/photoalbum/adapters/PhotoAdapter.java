package com.weatherbug.photoalbum.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.weatherbug.photoalbum.R;
import com.weatherbug.photoalbum.data.Photo;
import com.weatherbug.photoalbum.helpers.DrawableCache;
import com.weatherbug.photoalbum.views.PhotoView;

import java.util.List;

public class PhotoAdapter extends BaseAdapter {
    private final static int COLUMNS_PORTRAIT = 1;
    private final static int COLUMNS = 3;
    private final static int MIN_ROWS = 3;
    private final static int MIN_COUNT = COLUMNS * MIN_ROWS;

    private Context mContext;
    private List<Photo> mPhotosList = null;
    private int mMinCount;

    public PhotoAdapter(Context context) {
        mContext = context;
    }

    public void updatePhotos(List<Photo> photos) {
        mPhotosList = photos;

        int numTiles = photos.size();
        if (numTiles > MIN_COUNT && numTiles % COLUMNS != 0) {
            mMinCount = (numTiles / COLUMNS + 1) * COLUMNS;
        } else {
            mMinCount = MIN_COUNT;
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mPhotosList == null ? mMinCount : Math.max(mMinCount, mPhotosList.size());
    }

    @Override
    public Object getItem(int position) {
        return position < 0 || mPhotosList == null || position >= mPhotosList.size() ?
                null : mPhotosList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position < 0 || mPhotosList == null || position >= mPhotosList.size() ?
                0 : position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        if (position < 0 || position >= mPhotosList.size() && mPhotosList == null) {
            return convertView;
        }

        PhotoView photoView;

        if (convertView == null || !(convertView instanceof PhotoView)) {
            photoView = new PhotoView(mContext);
        } else {
            photoView = (PhotoView) convertView;
        }

        setDetails(container, position, photoView);

        return photoView;
    }

    private void setDetails(final ViewGroup container, final int position, final PhotoView photoView) {
        Photo photo = position >= 0 && position < mPhotosList.size() ? mPhotosList.get(position) : null;
        photoView.resetViews();

        if (photo == null) {
            photoView.hideViews();
            return;
        }

        photoView.setPhotoTitle(mContext.getString(R.string.title) + ":" + photo.getTitle());

        DrawableCache.getInstance().loadImageAsync(mContext, photo.getFilename(), photoView);

        photoView.showViews();
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GridView) container).performItemClick(v, position, position);
            }
        });
    }
}
