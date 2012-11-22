package com.lopefied.pepemon;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.lopefied.pepemon.adapter.AlbumListAdapter;
import com.lopefied.pepemon.adapter.AlbumListAdapter.IAlbumListAdapter;
import com.lopefied.pepemon.model.Album;
import com.lopefied.pepemon.task.GetAlbumsTask;
import com.lopefied.pepemon.task.GetAlbumsTask.IAlbumDownloader;

/**
 * 
 * @author Lope Chupijay Emano
 * 
 */
public class MainActivity extends Activity {

    Facebook facebook = new Facebook("195085947295131");
    AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);

    private SharedPreferences mPrefs;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        /*
         * Get existing access_token if any
         */
        mPrefs = getPreferences(MODE_PRIVATE);
        String accessToken = mPrefs.getString("access_token", null);
        System.out.println("Access token : " + accessToken);
        long expires = mPrefs.getLong("access_expires", 0);
        if (accessToken != null) {
            facebook.setAccessToken(accessToken);
        }
        if (expires != 0) {
            facebook.setAccessExpires(expires);
        }

        /*
         * Only call authorize if the access_token has expired.
         */
        if (!facebook.isSessionValid()) {

            facebook.authorize(this, new String[] {}, new DialogListener() {
                @Override
                public void onComplete(Bundle values) {
                    System.out.println("Return access token : "
                            + facebook.getAccessToken());
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString("access_token", facebook.getAccessToken());
                    editor.putLong("access_expires",
                            facebook.getAccessExpires());
                    editor.commit();
                }

                @Override
                public void onFacebookError(FacebookError error) {
                    System.out.println("onFBError");
                }

                @Override
                public void onError(DialogError e) {
                    e.printStackTrace();
                }

                @Override
                public void onCancel() {
                    System.out.println("onCancel");
                }
            });
        }
        if (accessToken != null)
            downloadAlbum(accessToken);
    }

    private void downloadAlbum(final String accessToken) {
        String URL = "https://graph.facebook.com/pepemon2/albums&access_token="
                + accessToken + "?limit=10";
        final ProgressDialog progressDialog = new ProgressDialog(this);
        IAlbumDownloader albumDownloaderListener = new IAlbumDownloader() {

            @Override
            public void noMoreAlbums() {
                progressDialog.dismiss();
            }

            @Override
            public String getFBAccessToken() {
                return accessToken;
            }

            @Override
            public void foundAlbums(List<Album> albumList) {
                Toast.makeText(getApplicationContext(),
                        "album list size : " + albumList.size(),
                        Toast.LENGTH_LONG).show();
                downloadAndDisplayPictures(albumList);
            }
        };
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Downloading albums..");
        progressDialog
                .setProgressStyle(ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        GetAlbumsTask task = new GetAlbumsTask(albumDownloaderListener,
                progressDialog);
        task.execute(URL);
    }

    private void downloadAndDisplayPictures(List<Album> albumList) {
        final String accessToken = mPrefs.getString("access_token", null);
        listView = (ListView) findViewById(R.id.listView);
        IAlbumListAdapter albumListListener = new IAlbumListAdapter() {
            @Override
            public String getFBToken() {
                return accessToken;
            }
        };
        AlbumListAdapter adapter = new AlbumListAdapter(this, R.layout.item_album,
                albumList, albumListListener);
        listView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebook.authorizeCallback(requestCode, resultCode, data);
    }

}
