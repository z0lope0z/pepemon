package com.lopefied.pepemon;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.lopefied.pepemon.adapter.PhotoListAdapter;
import com.lopefied.pepemon.adapter.PhotoListAdapter.IPhotoListAdapter;
import com.lopefied.pepemon.model.Photo;
import com.lopefied.pepemon.task.GetAlbumPhotosTask;
import com.lopefied.pepemon.task.GetAlbumPhotosTask.IAlbumPhotosDownloader;

/**
 * 
 * @author Lope Chupijay Emano
 * 
 */
public class AlbumPhotosActivity extends Activity {
    public static final String TAG = AlbumPhotosActivity.class.getSimpleName();
    public static final String ALBUM_ID = "album_id";

    private SharedPreferences mPrefs;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();
    }

    private void init() {
        Bundle extras = getIntent().getExtras();
        String albumID = null;
        if (extras != null) {
            albumID = extras.getString(ALBUM_ID);
            Log.i(TAG, "Got albumID : " + albumID);

            /*
             * Get existing access_token if any
             */
            mPrefs = getSharedPreferences("com.lopefied.pepemon",
                    MODE_WORLD_READABLE);
            String accessToken = mPrefs.getString("access_token", null);
            Log.i(TAG, "Got access token : " + accessToken);

            if (albumID != null) {
                downloadAlbumPhotos(accessToken, albumID);
            } else {
                Log.e(TAG, "Null album ID received");
            }
        }
    }

    private void downloadAlbumPhotos(final String accessToken,
            final String albumID) {
        String URL = "https://graph.facebook.com/" + albumID
                + "/photos?access_token=" + accessToken;
        System.out.println("using url : " + URL);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        IAlbumPhotosDownloader albumPhotosDownloaderListener = new IAlbumPhotosDownloader() {

            @Override
            public void noMoreAlbumPhotos() {
                progressDialog.dismiss();
            }

            @Override
            public String getFBAccessToken() {
                return accessToken;
            }

            @Override
            public void foundAlbumPhotos(List<Photo> photoList) {
                Log.i(TAG, "Received photos : " + photoList.size());
                downloadAndDisplayPictures(photoList);
            }
        };
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Downloading photos..");
        progressDialog
                .setProgressStyle(ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        GetAlbumPhotosTask task = new GetAlbumPhotosTask(
                albumPhotosDownloaderListener, progressDialog);
        task.execute(URL);
    }

    private void downloadAndDisplayPictures(List<Photo> photoList) {
        final String accessToken = mPrefs.getString("access_token", null);
        listView = (ListView) findViewById(R.id.listView);
        IPhotoListAdapter albumListAdapterListener = new IPhotoListAdapter() {
            @Override
            public String getFBToken() {
                return accessToken;
            }
        };
        PhotoListAdapter adapter = new PhotoListAdapter(this,
                R.layout.item_album, photoList, albumListAdapterListener);
        listView.setAdapter(adapter);
    }

}
