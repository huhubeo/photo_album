package com.weatherbug.photoalbum.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.weatherbug.photoalbum.R;
import com.weatherbug.photoalbum.data.Photo;
import com.weatherbug.photoalbum.helpers.DrawableCache;

public class PhotoDetailsFragment extends BaseFragment {

    private Photo mPhoto;
    private ImageButton mCloseButton;
    private ImageView mPhotoImage;
    private TextView mPhotoTitle;
    private TextView mPhotoDescription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.photo_details_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCloseButton = (ImageButton) findViewById(R.id.close_button);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFragment();
            }
        });

        mPhotoImage = (ImageView) findViewById(R.id.photo_image);
        mPhotoTitle = (TextView) findViewById(R.id.photo_title);
        mPhotoDescription = (TextView) findViewById(R.id.photo_description);

        if (savedInstanceState != null && savedInstanceState.getSerializable("photo") != null) {
            mPhoto = (Photo) savedInstanceState.getSerializable("photo");
        }

        if (mPhoto != null) {
            DrawableCache.getInstance().loadImageAsync(getMainActivity(), mPhoto.getFilename(), mPhotoImage);
            mPhotoTitle.setText(getString(R.string.title) + ":" + mPhoto.getTitle());
            mPhotoDescription.setText(getString(R.string.description) + ":" + mPhoto.getDescription());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putSerializable("photo", mPhoto);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void setPhoto(Photo photo) {
        mPhoto = photo;
    }
}
