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

import com.lopefied.pepemon.model.Photo;

/**
 * 
 * @author Lope Chupijay Emano
 * 
 */
public class GetAlbumPhotosTask extends AsyncTask<String, Void, List<Photo>> {
    public static final String PEPEMON_ID = "pepemon2";

    private ProgressDialog progressDialog;
    private IAlbumPhotosDownloader albumPhotosDownloader;

    public GetAlbumPhotosTask(IAlbumPhotosDownloader albumPhotosDownloader,
            ProgressDialog progressDialog) {
        this.albumPhotosDownloader = albumPhotosDownloader;
        this.progressDialog = progressDialog;
    }

    @Override
    protected void onPreExecute() {
        // SHOW THE PROGRESS BAR (SPINNER) WHILE LOADING ALBUMS
        progressDialog.show();
    }

    @Override
    protected List<Photo> doInBackground(String... params) {
        List<Photo> albumPhotoList = new ArrayList<Photo>();

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
                    albumPhotosDownloader.noMoreAlbumPhotos();

                } else {
                    // PAGING JSONOBJECT
                    Photo photo;

                    for (int i = 0; i < JAAlbums.length(); i++) {
                        JSONObject JOPhoto = JAAlbums.getJSONObject(i);
                        if (JOPhoto.has("picture")) {
                            photo = new Photo();
                            // GET THE ALBUM ID
                            if (JOPhoto.has("id")) {
                                photo.setPhotoID(JOPhoto.getString("id"));
                            } else {
                                photo.setPhotoID(null);
                            }
                            if (JOPhoto.has("images")) {
                                // GET THE PHOTO ARRAY
                                JSONArray JAPhotoSizes = JOPhoto
                                        .getJSONArray("images");
                                for (int j = 0; j < JAPhotoSizes.length(); j++) {
                                    JSONObject photoJSON = JAPhotoSizes
                                            .getJSONObject(j);
                                    System.out.println(photoJSON.toString());
                                    if ((photoJSON.has("height"))
                                            && photoJSON.has("width")) {
                                        if (photoJSON.getInt("height") > 400)
                                            photo.setPhotoURL(photoJSON
                                                    .getString("source"));
                                        System.out
                                                .println("saving photo with url : "
                                                        + photo.getPhotoURL());
                                        albumPhotoList.add(photo);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return albumPhotoList;
    }

    @Override
    protected void onPostExecute(List<Photo> albumPhotoList) {
        // HIDE THE PROGRESS BAR (SPINNER) AFTER LOADING ALBUMS
        progressDialog.hide();
        albumPhotosDownloader.foundAlbumPhotos(albumPhotoList);
        albumPhotosDownloader.noMoreAlbumPhotos();
    }

    public interface IAlbumPhotosDownloader {
        public String getFBAccessToken();

        public void noMoreAlbumPhotos();

        public void foundAlbumPhotos(List<Photo> photoList);
    }

}