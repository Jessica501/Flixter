package com.example.flixter.models;

import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

@Parcel // indicates class is Parceable
public class Movie {
    // fields must be public for pareceler
    String posterPath;
    String backdropPath;
    String title;
    String overview;
    Double voteAverage;
    Integer id;
    String videoId;

    public static final String TAG = "Movie";

    // no-arg, empty constructor required for Parceler
    public Movie() {
    }


    public Movie(JSONObject jsonObject) throws JSONException {
        posterPath = jsonObject.getString("poster_path");
        backdropPath = jsonObject.getString("backdrop_path");
        title = jsonObject.getString("title");
        overview = jsonObject.getString("overview");
        voteAverage = jsonObject.getDouble("vote_average");
        id = jsonObject.getInt("id");
        setVideoId();
    }

    public static List<Movie> fromJSONArray(JSONArray movieJsonArray) throws JSONException {
        List<Movie> movies = new ArrayList<>();
        for(int i = 0; i < movieJsonArray.length(); i++) {
            movies.add(new Movie(movieJsonArray.getJSONObject(i)));
        }
        return movies;
    }

    private void  setVideoId() {
        // retrieve video from the API
        String videoUrl = String.format("https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed", this.id);
        Log.d(TAG, videoUrl);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(videoUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    videoId = getFirstYoutubeId(results);
                    Log.d(TAG, String.format("Youtube ID: %s", videoId));

                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });
    }

    // Returns the first video id that is from the site "YouTube"
    private String getFirstYoutubeId(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                if (jsonArray.getJSONObject(i).getString("site").equals("YouTube")) {
                    return jsonArray.getJSONObject(i).getString("key");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }


    public String getPosterPath() {
        return String.format("https://image.tmdb.org/t/p/original/%s", posterPath);
    }

    public String getBackdropPath() {
        return String.format("https://image.tmdb.org/t/p/original/%s", backdropPath);
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public Integer getId() {
        return id;
    }

    public String getVideoId() {
        return videoId;
    }
}
