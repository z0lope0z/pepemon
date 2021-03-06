package com.lopefied.pepemon.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class FBParseUtils {
    public static final String TAG = FBParseUtils.class.getSimpleName();

    public static String extractURL(String pidJSONReply) throws JSONException {
        String returnURL = null;
        JSONObject JOTemp = new JSONObject(pidJSONReply);
        JSONArray pictureJSONArray = JOTemp.getJSONArray("data");
        for (int i = 0; i < pictureJSONArray.length(); i++) {
            JSONObject pictureJSON = pictureJSONArray.getJSONObject(i);
            returnURL = extractURLFromImageObject(pictureJSON);
        }
        return returnURL;
    }

    public static String extractURLFromImageObject(JSONObject pictureJSON)
            throws JSONException {
        String returnURL = null;
        if (pictureJSON.has("images")) {
            // GET THE PHOTO ARRAY
            JSONArray JAPhotoSizes = pictureJSON.getJSONArray("images");
            for (int j = 0; j < JAPhotoSizes.length(); j++) {
                JSONObject photoJSON = JAPhotoSizes.getJSONObject(j);
                if ((photoJSON.has("height")) && photoJSON.has("width")) {
                    if ((photoJSON.getInt("height") > 300)
                            && (photoJSON.getInt("height") < 600)) {
                        returnURL = photoJSON.getString("source");
                        Log.i(TAG,
                                "saving photo with url : " + returnURL
                                        + " using height : "
                                        + photoJSON.getInt("height"));
                        return returnURL;
                    } else if (returnURL == null)
                        returnURL = photoJSON.getString("source");
                }
            }
        }
        return returnURL;
    }
}
