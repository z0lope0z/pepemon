package com.lopefied.pepemon;

import java.sql.SQLException;
import java.util.List;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.lopefied.pepemon.db.DBHelper;
import com.lopefied.pepemon.db.model.Album;
import com.lopefied.pepemon.db.model.Photo;
import com.lopefied.pepemon.fragment.ViewPhotoFragmentAdapter;
import com.lopefied.pepemon.provider.AlbumPhotosListener;
import com.lopefied.pepemon.provider.AlbumPhotosProvider;
import com.lopefied.pepemon.provider.impl.AlbumPhotosProviderImpl;
import com.lopefied.pepemon.service.AlbumService;
import com.lopefied.pepemon.service.PhotoService;
import com.lopefied.pepemon.service.exception.NoAlbumExistsException;
import com.lopefied.pepemon.service.impl.AlbumServiceImpl;
import com.lopefied.pepemon.service.impl.PhotoServiceImpl;

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

    private static final int POSITION_TO_LOAD = 2;

    private SharedPreferences mPrefs;

    private ViewPhotoFragmentAdapter fragmentAdapter;
    private ViewPager mViewPager;
    private Album album;
    private DBHelper dbHelper;
    private PhotoService photoService;
    private AlbumService albumService;
    private String accessToken;

    private AlbumPhotosProvider albumPhotosProvider;
    private ProgressDialog progressDialog;
    private AlbumPhotosListener albumPhotosListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_photo_main);
        mPrefs = getSharedPreferences("com.lopefied.pepemon",
                MODE_WORLD_READABLE);
        Bundle extras = getIntent().getExtras();
        String albumID = extras.getString(ALBUM_ID);
        Integer photoID = extras.getInt(CURRENT_PHOTO_ID);
        accessToken = mPrefs.getString("access_token", null);
        Log.i(TAG, "Got access token : " + accessToken);
        Log.i(TAG, "Got albumID : " + albumID);
        Log.i(TAG, "Got photoID : " + photoID);
        dbHelper = (DBHelper) OpenHelperManager.getHelper(this, DBHelper.class);
        try {
            albumService = new AlbumServiceImpl(dbHelper.getAlbumDao());
            photoService = new PhotoServiceImpl(dbHelper.getPhotoDao());
            album = albumService.getAlbum(albumID);
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
        initProvider();
    }

    private void initProvider() {
        progressDialog = new ProgressDialog(getApplicationContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Downloading photos..");
        progressDialog
                .setProgressStyle(ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        albumPhotosProvider = new AlbumPhotosProviderImpl(photoService,
                progressDialog, accessToken);
        albumPhotosListener = new AlbumPhotosListener() {

            @Override
            public void noMorePhotos() {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "No more photos",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void error(String message) {
                Toast.makeText(getApplicationContext(), message,
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void addNewPhotos(List<Photo> photoList) {
                Log.i(TAG, "Received photos : " + photoList.size());
                if (photoList.size() > 0) {
                    fragmentAdapter.addPhotos(photoList);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "No more photos to load", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void startingDownload() {
                Toast.makeText(getApplicationContext(),
                        "Loading more photos..", Toast.LENGTH_LONG).show();
            }
        };

        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                Photo currentPhoto = fragmentAdapter.getPhoto(mViewPager
                        .getCurrentItem());
                if (position > fragmentAdapter.getCount() - POSITION_TO_LOAD) {
                    albumPhotosProvider.loadMore(albumPhotosListener,
                            currentPhoto, album, fragmentAdapter.getCount());
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

}
