package com.bignerdranch.photogallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FlickrFetchr {
    private static final String TAG = "FlickrFetchr";
    private static final String API_KEY = "c7bd15f062392f918ba54bef8c3106a1";
    //secret "0c71d9ce2caef7b4"
    private static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent"; // cambios de página 522
    private static final String SEARCH_METHOD = "flickr.photos.search"; // cambios de página 522
    private static final Uri ENDPOINT = Uri
            .parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s")
            .build(); // cambios de página 522


    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

  // public List<GalleryItem> fetchItems() {
  public List<GalleryItem> downloadGalleryItems(String url) { // se cambia en página 523
        List<GalleryItem> items = new ArrayList<>();
        try {

           //esto se cambia en pagina 522 String url = Uri.parse("https://api.flickr.com/services/rest/")
             //       .buildUpon()
               //     .appendQueryParameter("method", "flickr.photos.getRecent")

                 //   .appendQueryParameter("api_key", API_KEY)
                   // .appendQueryParameter("format", "json")
               //     .appendQueryParameter("nojsoncallback", "1")
                 //   .appendQueryParameter("extras", "url_s")
                   // .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
                JSONObject jsonBody = new JSONObject(jsonString);
                parseItems(items, jsonBody);
            } catch (JSONException je) {
                Log.e(TAG, "Failed to parse JSON", je);

        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }
        return items;
    }

    private String buildUrl(String method, String query){ // se crea en página 523
        Uri.Builder uriBuilder = ENDPOINT.buildUpon()
                .appendQueryParameter("method", method); // se crea en página 523

        if (method.equals(SEARCH_METHOD)){
            uriBuilder.appendQueryParameter("text", query);// se crea en página 523
        }

        return uriBuilder.build().toString();// se crea en página 523
    } // se crea en página 523 hasta aquí

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItem> fetchRecentPhotos(){ // se crea en pagina 524
        String url = buildUrl(FETCH_RECENTS_METHOD, null);
        return downloadGalleryItems(url);
    }

    public List<GalleryItem> searchPhotos(String query){ // se crea en pagina 524
        String url = buildUrl(SEARCH_METHOD, query);
        return downloadGalleryItems(url);
    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws JSONException {
        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photosJsonArray = photosJsonObject.getJSONArray("photo");

        for(int i = 0; i < photosJsonArray.length(); i++) {
            JSONObject photoJsonObject = photosJsonArray.getJSONObject(i);

            GalleryItem item = new GalleryItem();
            item.setmId(photoJsonObject.getString("id"));
            item.setmCaption(photoJsonObject.getString("title"));

            if(!photoJsonObject.has("url_s")) {
                continue;
            }
            item.setmUrl(photoJsonObject.getString("url_s"));
            items.add(item);
        }
    }
}
