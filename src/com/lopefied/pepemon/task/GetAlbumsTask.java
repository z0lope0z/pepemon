package com.lopefied.pepemon.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.lopefied.pepemon.model.Album;

/**
 * 
 * @author Lope Chupijay Emano
 * 
 */
public class GetAlbumsTask extends AsyncTask<String, Void, List<Album>> {
    public static final String PEPEMON_ID = "pepemon2";

    private ProgressDialog progressDialog;
    private IAlbumDownloader albumDownloader;
    private Boolean stopLoadingData;
    private Boolean loadingMore;

    public GetAlbumsTask(IAlbumDownloader albumDownloader,
            ProgressDialog progressDialog) {
        this.albumDownloader = albumDownloader;
        this.progressDialog = progressDialog;
    }

    @Override
    protected void onPreExecute() {
        // SHOW THE PROGRESS BAR (SPINNER) WHILE LOADING ALBUMS
        progressDialog.show();
    }

    @Override
    protected List<Album> doInBackground(String... params) {
        List<Album> albumList = new ArrayList<Album>();

        // CHANGE THE LOADING MORE STATUS TO PREVENT DUPLICATE CALLS FOR
        // MORE DATA WHILE LOADING A BATCH
        loadingMore = true;

        // SET THE INITIAL URL TO GET THE FIRST LOT OF ALBUMS
        String URL = params[0];
        try {

            HttpClient hc = new DefaultHttpClient();
            HttpGet get = new HttpGet(URL);
            HttpResponse rp = hc.execute(get);

            if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String queryAlbums = EntityUtils.toString(rp.getEntity());

                JSONObject JOTemp = new JSONObject(queryAlbums);

                JSONArray JAAlbums = JOTemp.getJSONArray("data");

                if (JAAlbums.length() == 0) {
                    stopLoadingData = true;
                    albumDownloader.noMoreAlbums();

                } else {
                    // PAGING JSONOBJECT
                    if (JOTemp.has("paging")) {
                        JSONObject JOPaging = JOTemp.getJSONObject("paging");

                        if (JOPaging.has("next")) {
                            String initialpagingURL = JOPaging
                                    .getString("next");

                            String[] parts = initialpagingURL.split("limit=10");
                            String getLimit = parts[1];

                            String pagingURL = "https://graph.facebook.com/"
                                    + PEPEMON_ID + "/albums&access_token="
                                    + albumDownloader.getFBAccessToken()
                                    + "?limit=10" + getLimit;

                        } else {
                            stopLoadingData = true;
                            albumDownloader.noMoreAlbums();
                        }
                    } else {
                        stopLoadingData = true;
                        albumDownloader.noMoreAlbums();
                    }

                    Album albums;

                    for (int i = 0; i < JAAlbums.length(); i++) {
                        JSONObject JOAlbums = JAAlbums.getJSONObject(i);

                        if (JOAlbums.has("link")) {
                            albums = new Album();
                            // GET THE ALBUM ID
                            if (JOAlbums.has("id")) {
                                albums.setAlbumID(JOAlbums.getString("id"));
                            } else {
                                albums.setAlbumID(null);
                            }

                            // GET THE ALBUM NAME
                            if (JOAlbums.has("name")) {
                                albums.setAlbumName(JOAlbums.getString("name"));
                            } else {
                                albums.setAlbumName(null);
                            }

                            // GET THE ALBUM COVER PHOTO
                            if (JOAlbums.has("cover_photo")) {
                                albums.setAlbumCover("https://graph.facebook.com/"
                                        + JOAlbums.getString("cover_photo")
                                        + "/picture?type=normal"
                                        + "&access_token="
                                        + albumDownloader.getFBAccessToken());
                            } else {
                                albums.setAlbumCover("https://graph.facebook.com/"
                                        + JOAlbums.getString("id")
                                        + "/picture?type=album"
                                        + "&access_token="
                                        + albumDownloader.getFBAccessToken());
                            }
                            // GET THE ALBUM'S PHOTO COUNT
                            if (JOAlbums.has("count")) {
                                albums.setAlbumPhotoCount(JOAlbums
                                        .getString("count"));
                            } else {
                                albums.setAlbumPhotoCount("0");
                            }
                            albumList.add(albums);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return albumList;
    }

    @Override
    protected void onPostExecute(List<Album> albumList) {
        // // SET THE ADAPTER TO THE LISTVIEW
        // lv.setAdapter(adapter);
        // CHANGE THE LOADING MORE STATUS
        loadingMore = false;
        // HIDE THE PROGRESS BAR (SPINNER) AFTER LOADING ALBUMS
        progressDialog.hide();
        albumDownloader.foundAlbums(albumList);
        albumDownloader.noMoreAlbums();
    }

    public interface IAlbumDownloader {
        public String getFBAccessToken();

        public void noMoreAlbums();

        public void foundAlbums(List<Album> albumList);
    }

}