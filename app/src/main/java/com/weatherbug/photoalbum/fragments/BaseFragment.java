package com.weatherbug.photoalbum.fragments;

import android.app.Fragment;
import android.view.View;

import com.weatherbug.photoalbum.activities.MainActivity;

public class BaseFragment extends Fragment {

    protected MainActivity getMainActivity() {
        return (MainActivity)getActivity();
    }

    protected final View findViewById(int id) {
        return getView() != null ? getView().findViewById(id) : null;
    }

    protected void closeFragment() {
        removeListeners();
        getMainActivity().getFragmentManager()
                .beginTransaction()
                .remove(this)
                .commit();
    }

    protected void removeListeners() {
        //will be override by children
    }
}
