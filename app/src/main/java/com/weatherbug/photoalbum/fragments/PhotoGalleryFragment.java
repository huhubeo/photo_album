package com.weatherbug.photoalbum.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.weatherbug.photoalbum.R;
import com.weatherbug.photoalbum.adapters.PhotoAdapter;
import com.weatherbug.photoalbum.data.Photo;
import com.weatherbug.photoalbum.tasks.ImageMetadataDownloader;

import java.util.List;

public class PhotoGalleryFragment extends BaseFragment implements ImageMetadataDownloader.ImageMetadataDownloaderListener{

    private GridView mGridPhotos;
    private PhotoAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.photos_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mGridPhotos = (GridView) findViewById(R.id.grid_photo);

        ImageMetadataDownloader downloader = new ImageMetadataDownloader(this);
        downloader.execute();
    }

    @Override
    public void onPhotosLoaded(List<Photo> list) {
        mAdapter = new PhotoAdapter(getMainActivity());

        initGrid();
        mAdapter.updatePhotos(list);
    }

    private void initGrid() {
        mGridPhotos.setAdapter(mAdapter);
        mGridPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Photo photo = (Photo) mAdapter.getItem(i);
                if (photo != null) {
                    openPhoto(photo);
                }
            }
        });
    }

    private void openPhoto(Photo photo) {
        getMainActivity().openPhoto(photo);
    }
}
