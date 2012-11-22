package com.lopefied.pepemon;

import com.lopefied.pepemon.util.ImageLoader;
import com.lopefied.pepemon.widgets.TouchImageView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * 
 * @author Lope Chupijay Emano
 * 
 */
public class ViewPhotoActivity extends Activity {
    public static final String TAG = ViewPhotoActivity.class.getSimpleName();
    public static final String PHOTO_URL = "photo_url";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_photo);
        init();
    }

    private void init() {
        Bundle extras = getIntent().getExtras();
        String photoURL = null;
        if (extras != null) {
            photoURL = extras.getString(PHOTO_URL);
            Log.i(TAG, "Got albumID : " + photoURL);
            if (photoURL != null) {
                TouchImageView imageView = (TouchImageView) findViewById(R.id.imageView);
                ImageLoader imageLoader = new ImageLoader(this);
                imageView.setImageBitmap(imageLoader.getBitmap(photoURL));
                imageView.setMaxZoom(4f);
            }
        }
    }

}
