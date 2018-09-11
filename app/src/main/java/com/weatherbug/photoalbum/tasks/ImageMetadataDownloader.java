package com.weatherbug.photoalbum.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherbug.photoalbum.data.Photo;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ImageMetadataDownloader extends AsyncTask<Void, Void, List> {
    private static final String TAG = ImageMetadataDownloader.class.getName();
    private static final String METADATA_LINK = "https://s3.amazonaws.com/sc.va.util.weatherbug.com/interviewdata/mobilecodingchallenge/sampledata.json";

    public interface ImageMetadataDownloaderListener {
        void onPhotosLoaded(List<Photo> list);
    }

    private ImageMetadataDownloaderListener mListener;

    public ImageMetadataDownloader(ImageMetadataDownloaderListener listener) {
        mListener = listener;
    }

    @Override
    protected List<Photo> doInBackground(Void... params) {
        List<Photo> photoList = new ArrayList<>();
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet post = new HttpGet(METADATA_LINK);
            post.addHeader("content-type", "application/json");

            ObjectMapper mapper = new ObjectMapper();


            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream inputStream = response.getEntity().getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);

                // Will store the data
                StringBuilder stringBuilder = new StringBuilder();

                String line = null;

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }

                String result = stringBuilder.toString();

                //create array of photo
                JSONArray array = new JSONArray(result);

                for(int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    Photo photo = mapper.readValue(obj.toString(), Photo.class);
                    photoList.add(photo);
                }
            } else {
                Log.d(TAG, "Download HttpResponse Error");
            }
        } catch (JSONException js) {
            Log.d(TAG, "Download Parser Error=" + js.getMessage());
        } catch (Exception es) {
            Log.d(TAG, "Download Error=" + es.getMessage());
        }
        return photoList;
    }

    @Override
    protected void onPostExecute(List list) {
        super.onPostExecute(list);
        if (mListener != null) {
            mListener.onPhotosLoaded(list);
        }
    }
}
