package com.lopefied.pepemon.util;

/**
 * 
 * @author Lope Chupijay Emano
 * 
 */
public class FBUtil {
    public static String generateImageURL(String imageID, String accessToken) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://graph.facebook.com/");
        stringBuilder.append(imageID);
        stringBuilder.append("/picture?type=normal&method=GET&access_token=");
        stringBuilder.append(accessToken);
        String returnURL = stringBuilder.toString();
        return returnURL;
    }
}
