package com.weatherbug.photoalbum.activities;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.weatherbug.photoalbum.R;
import com.weatherbug.photoalbum.data.Photo;
import com.weatherbug.photoalbum.fragments.PhotoDetailsFragment;
import com.weatherbug.photoalbum.fragments.PhotoGalleryFragment;
import com.weatherbug.photoalbum.helpers.DrawableCache;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        DrawableCache.getInstance().setMaxSize(width, height);

        PhotoGalleryFragment fragment = new PhotoGalleryFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_container, fragment).commit();
    }

    public void openPhoto(Photo photo) {
        PhotoDetailsFragment fragment = new PhotoDetailsFragment();
        fragment.setPhoto(photo);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.top_container, fragment).commit();
    }
}
