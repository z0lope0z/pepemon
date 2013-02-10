package com.lopefied.pepemon;

import java.sql.SQLException;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.lopefied.pepemon.db.DBHelper;
import com.lopefied.pepemon.db.model.Album;
import com.lopefied.pepemon.db.model.Photo;
import com.lopefied.pepemon.fragment.ViewPhotoFragmentAdapter;
import com.lopefied.pepemon.service.AlbumService;
import com.lopefied.pepemon.service.PhotoService;
import com.lopefied.pepemon.service.exception.NoAlbumExistsException;
import com.lopefied.pepemon.service.impl.AlbumServiceImpl;
import com.lopefied.pepemon.service.impl.PhotoServiceImpl;
import com.lopefied.pepemon.util.ImageLoader;
import com.lopefied.pepemon.widgets.TouchImageView;

/**
 * 
 * @author Lope Chupijay Emano
 * 
 */
public class ViewPhotoFragmentActivity extends FragmentActivity {
    public static final String TAG = ViewPhotoFragmentActivity.class
            .getSimpleName();
    public static final String ALBUM_ID = "album_id";
    public static final String PHOTO_URL = "photo_url";
    public static final String CURRENT_PHOTO_ID = "photo_fb_id";
    private ViewPhotoFragmentAdapter fragmentAdapter;
    private ViewPager mViewPager;
    private Album album;
    private DBHelper dbHelper;
    private PhotoService photoService;
    private AlbumService albumService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_photo_main);
        Bundle extras = getIntent().getExtras();
        String albumID = extras.getString(ALBUM_ID);
        Integer photoID = extras.getInt(CURRENT_PHOTO_ID);
        Log.i(TAG, "Got albumID : " + albumID);
        Log.i(TAG, "Got photoID : " + photoID);
        dbHelper = (DBHelper) OpenHelperManager.getHelper(this, DBHelper.class);
        try {
            albumService = new AlbumServiceImpl(dbHelper.getAlbumDao());
            photoService = new PhotoServiceImpl(dbHelper.getPhotoDao());
            Album album = albumService.getAlbum(albumID);
            List<Photo> photos = photoService.getAlbumPhotos(album);
            fragmentAdapter = new ViewPhotoFragmentAdapter(
                    getSupportFragmentManager(), photos);
            mViewPager = (ViewPager) findViewById(R.id.pager);
            int counter = 0;
            for (Photo photo : photos) {
                if (photo.getID() == photoID)
                    break;
                counter++;
            }
            mViewPager.setAdapter(fragmentAdapter);
            mViewPager.setCurrentItem(counter);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoAlbumExistsException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        Bundle extras = getIntent().getExtras();
        String photoURL = null;
        if (extras != null) {
            photoURL = extras.getString(PHOTO_URL);
            Log.i(TAG, "Got albumID : " + photoURL);
            if (photoURL != null) {
                TouchImageView imageView = (TouchImageView) findViewById(R.id.imageView);
                ImageLoader imageLoader = ImageLoader.getInstance(this);
                imageView.setImageBitmap(imageLoader.getBitmap(photoURL));
                imageView.setMaxZoom(4f);
            }
        }
    }

}
