package com.lopefied.pepemon.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.lopefied.pepemon.R;
import com.lopefied.pepemon.util.ImageLoader;
import com.lopefied.pepemon.widgets.TouchImageView;

public class PhotoFragment extends Fragment {
    public static final String ARG_IMAGE_URL = "image_url";
    public ImageLoader imageLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(R.layout.view_photo, container, false);
        Bundle args = getArguments();
        String imageURL = args.getString(ARG_IMAGE_URL);
        imageLoader = ImageLoader.getInstance(getActivity()
                .getApplicationContext());
        final TouchImageView imageView = ((TouchImageView) rootView
                .findViewById(R.id.imageView));
        imageLoader.displayImage(imageURL, imageView, ScaleType.FIT_CENTER);
        imageView.setMaxZoom(4f);
        return rootView;
    }

    static class ViewHolder {
        public ImageView imgProductThumbnail;
    }
}