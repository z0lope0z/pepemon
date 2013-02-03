package com.lopefied.pepemon;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.Toast;

import com.lopefied.pepemon.adapter.PhotoListAdapter;
import com.lopefied.pepemon.adapter.PhotoListAdapter.IPhotoListAdapter;
import com.lopefied.pepemon.db.DBHelper;
import com.lopefied.pepemon.db.model.Album;
import com.lopefied.pepemon.db.model.Photo;
import com.lopefied.pepemon.provider.AlbumPhotosListener;
import com.lopefied.pepemon.provider.AlbumPhotosProvider;
import com.lopefied.pepemon.provider.impl.AlbumPhotosProviderImpl;
import com.lopefied.pepemon.service.AlbumService;
import com.lopefied.pepemon.service.PhotoService;
import com.lopefied.pepemon.service.exception.NoAlbumExistsException;
import com.lopefied.pepemon.service.impl.AlbumServiceImpl;
import com.lopefied.pepemon.service.impl.PhotoServiceImpl;
import com.lopefied.pepemon.task.GetAlbumPhotosTask;
import com.lopefied.pepemon.util.PepemonUtils;

/**
 * 
 * @author Lope Chupijay Emano
 * 
 */
public class AlbumPhotosActivity extends Activity {
    public static final String TAG = AlbumPhotosActivity.class.getSimpleName();
    public static final String ALBUM_ID = "album_id";
    public static final Integer PAGE_COUNT = 8;

    private SharedPreferences mPrefs;
    private ListView listView;
    private Integer currentPage = 0;
    private String albumID = null;
    private String accessToken = null;
    private ProgressDialog progressDialog;
    private Boolean isDownloadingStuff = false;
    private PhotoListAdapter adapter;

    private AlbumPhotosProvider albumPhotosProvider;
    private PhotoService photoService;
    private AlbumService albumService;
    private AlbumPhotosListener albumPhotosListener;
    private Album album;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();
    }

    private void initExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            albumID = extras.getString(ALBUM_ID);
            Log.i(TAG, "Got albumID : " + albumID);
            /*
             * Get existing access_token if any
             */
            mPrefs = getSharedPreferences("com.lopefied.pepemon",
                    MODE_WORLD_READABLE);
            accessToken = mPrefs.getString("access_token", null);
            Log.i(TAG, "Got access token : " + accessToken);
        }
    }

    private void initViews() {
        progressDialog = new ProgressDialog(this);
        listView = (ListView) findViewById(R.id.listView);
        final String accessToken = mPrefs.getString("access_token", null);
        IPhotoListAdapter albumListAdapterListener = new IPhotoListAdapter() {
            @Override
            public String getFBToken() {
                return accessToken;
            }

            @Override
            public void selectPhoto(Photo photo) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(),
                        ViewPhotoActivity.class);
                intent.putExtra(ViewPhotoActivity.PHOTO_URL,
                        photo.getPhotoURL());
                startActivity(intent);
            }
        };
        adapter = new PhotoListAdapter(this, R.layout.item_album,
                new ArrayList<Photo>(), albumListAdapterListener);
        listView.setAdapter(adapter);

        listView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {
            }

            @Override
            public void onScroll(AbsListView listView, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {
                switch (listView.getId()) {
                case R.id.listView:
                    final int lastItem = firstVisibleItem + visibleItemCount;
                    if ((lastItem >= totalItemCount - 2)
                            && (totalItemCount != 0)) {
                        Photo photo = (Photo) listView.getAdapter().getItem(
                                totalItemCount - 1);
                        if (photo != null) {
                            if (!isDownloadingStuff) {
                                currentPage = currentPage
                                        + (GetAlbumPhotosTask.PAGE_COUNT + 1);
                                Photo lastPhoto = photo;
                                albumPhotosProvider.loadMore(
                                        albumPhotosListener, lastPhoto, album,
                                        PAGE_COUNT, currentPage);
                                Toast.makeText(getApplicationContext(),
                                        "Loading more items..",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }
        });
    }

    private void initProviders() {
        DBHelper dbHelper = new DBHelper(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Downloading photos..");
        progressDialog
                .setProgressStyle(ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);

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
                    loadPhotos(photoList);
                    isDownloadingStuff = false;
                } else {
                    Toast.makeText(getApplicationContext(),
                            "No more photos to load", Toast.LENGTH_LONG).show();
                }
            }
        };
        try {
            photoService = new PhotoServiceImpl(dbHelper.getPhotoDao());
            albumService = new AlbumServiceImpl(dbHelper.getAlbumDao());
            albumPhotosProvider = new AlbumPhotosProviderImpl(photoService,
                    progressDialog, accessToken);
            album = albumService.getAlbum(albumID);
            albumPhotosProvider.loadMore(albumPhotosListener, null, album,
                    PAGE_COUNT, currentPage);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoAlbumExistsException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        initExtras();
        initViews();
        if (albumID != null) {
            initProviders();
        } else {
            Log.e(TAG, "Null album ID received");
        }

    }

    private void loadPhotos(List<Photo> photoList) {
        List<Photo> combinedList = PepemonUtils.combineDTOList(
                adapter.getList(), photoList);
        adapter.set(combinedList);
        adapter.notifyDataSetChanged();
    }

}
