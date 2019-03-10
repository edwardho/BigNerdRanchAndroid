package com.bignerdranch.android.photogallery;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FlickrFetchr {

    private static final String TAG = "FlickrFetchr";

    private static final String API_KEY = "edee12cc0343ce07f5f41f14a151fd8f";

    // Fetches bytes from url
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        // Open HTTP connection with url spec passed
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            // Make the connection
            // Connection not made until getInputSteam made for get calls
            // Connection not made until getOutputStream for post calls
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            // If responsecode is not 200 OK, respond with IOException
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            // While bytes read in input stream, write the bytes to output stream
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            // Close output stream
            out.close();
            // Return output byte array
            return out.toByteArray();
        }
        // Disconnect Connection
        finally {
            connection.disconnect();
        }
    }

    // Converts bytes fetched by getUrlBytes to a string
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItem> fetchItems() {

        List<GalleryItem> items = new ArrayList<>();

        // try to build the url string and make the request using getUrlString
        // Uri.Builder properly escapes the url string
        try {
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();

            // Get json string output
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            // Converts jsonString into a new JSONObject
            //JSONObject jsonBody = new JSONObject(jsonString);
            // Parse jsonBody into a list of Gallery Items
            //parseItems(items, jsonBody);

            // Create a new JSON parser
            JsonParser parser = new JsonParser();
            // Parse JSON to get photo object
            JsonElement photoJsonElement = parser.parse(jsonString)
                    .getAsJsonObject().get("photos")
                    .getAsJsonObject().get("photo");

            // Create new Gson instance
            Gson gson = new Gson();
            // Get list type from GalleryItem arraylist
            Type listType = new TypeToken<ArrayList<GalleryItem>>(){}.getType();
            // Set list of items to that of the photo json elements
            items = gson.fromJson(photoJsonElement, listType);
        }
        catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }
        //catch (JSONException je) {
        //    Log.e(TAG, "Failed to parse JSON", je);
        //}

        // Return gallery items
        return items;
    }

    /*
    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws IOException, JSONException {

        // Get photos json object from passed json body
        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        // Get photo json array from the photos json object
        JSONArray photosJsonArray = photosJsonObject.getJSONArray("photo");

        // For every item in the photosJsonArray
        for (int i = 0; i < photosJsonArray.length(); i++) {
            // Get the json photo object
            JSONObject photoJsonObject = photosJsonArray.getJSONObject(i);

            // Create a new Gallery Item
            GalleryItem item = new GalleryItem();
            // Set the id using the photo json object id value
            item.setId(photoJsonObject.getString("id"));
            // Set the caption using the photo json object title value
            item.setCaption(photoJsonObject.getString("title"));

            // If the photo json object does not have "url_s", continue
            if (!photoJsonObject.has("url_s")) {
                continue;
            }

            // Set the url of the photo using the photo json object url_s value
            item.setUrl(photoJsonObject.getString("url_s"));
            // Add item to list of items
            items.add(item);
        }
    }*/
}
