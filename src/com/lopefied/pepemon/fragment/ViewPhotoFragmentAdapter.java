package com.lopefied.pepemon.fragment;

import java.util.List;

import com.lopefied.pepemon.db.model.Photo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ViewPhotoFragmentAdapter extends FragmentStatePagerAdapter {
    private List<Photo> photos;

    public ViewPhotoFragmentAdapter(FragmentManager fm, List<Photo> photos) {
        super(fm);
        this.photos = photos;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new PhotoFragment();
        if (i < getCount()) {
            Bundle args = new Bundle();
            args.putString(PhotoFragment.ARG_IMAGE_URL, photos.get(i)
                    .getPhotoURL());
            fragment.setArguments(args);
        }
        return fragment;
    }

    public Photo getPhoto(int position) {
        return photos.get(position);
    }

    public void addPhotos(List<Photo> photos) {
        this.photos.addAll(photos);
    }

    @Override
    public int getCount() {
        return photos.size();
    }

}