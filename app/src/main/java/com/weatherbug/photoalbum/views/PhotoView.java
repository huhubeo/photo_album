package com.weatherbug.photoalbum.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weatherbug.photoalbum.R;

public class PhotoView extends RelativeLayout{

    private ImageView mImage;
    private ProgressBar mProgressBar;
    private TextView mPhotoTitle;

    public PhotoView(Context context) {
        super(context);

        loadLayout();
    }

    protected void loadLayout() {
        inflateLayout();

        mImage = (ImageView) findViewById(R.id.photo_image);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar) ;
        mProgressBar.setVisibility(GONE);
        mPhotoTitle = (TextView) findViewById(R.id.photo_title);
    }

    protected void inflateLayout() {
        LayoutInflater.from(getContext()).inflate(R.layout.photo_tile, this, true);
    }

    public void setImage(Drawable drawable) {
        showHideProgressBar(false);
        mImage.setVisibility(VISIBLE);
        mImage.setImageDrawable(drawable);
    }

    public void setPhotoTitle(String title) {
        mPhotoTitle.setText(title);
    }

    public ImageView getImageView() {
        return mImage;
    }

    public void resetViews() {
        mPhotoTitle.setText("");
        mImage.setVisibility(GONE);
    }

    public void hideViews() {
        resetViews();
        mImage.setVisibility(GONE);
    }

    public void showViews() {
        mImage.setVisibility(VISIBLE);
    }

    public void showHideProgressBar(boolean isShown) {
        mProgressBar.setVisibility(isShown ? VISIBLE : GONE);
    }
}
