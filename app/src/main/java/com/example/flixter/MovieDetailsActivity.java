package com.example.flixter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixter.databinding.ActivityMovieDetailsBinding;
import com.example.flixter.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class MovieDetailsActivity extends AppCompatActivity {

    Movie movie;
    Context context;
    public static final String TAG = "MovieDetailsActivity";


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityMovieDetailsBinding binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        context = this.getApplicationContext();

        // unwrap the movie passed in via intent
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        // set the title and overview
        binding.tvTitle.setText(movie.getTitle());
        binding.tvOverview.setText(movie.getOverview());
        binding.tvOverview.setMovementMethod(new ScrollingMovementMethod());
        binding.tvPopularity.setText("Popularity: " + movie.getPopularity());

        // vote average is 0-10, convert to 0-5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        binding.rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);

        // set the image
        Resources res = context.getResources();
        Drawable placeholder = ResourcesCompat.getDrawable(res, R.drawable.flicks_backdrop_placeholder, null);
        int radius = 30;
        int margin = 5;
        Glide
                .with(context)
                .load(movie.getBackdropPath())
                .placeholder(placeholder)
                .transform(new RoundedCornersTransformation(radius, margin))
                .into(binding.ivBackdrop);

        // retrieve video from the API
        String videoUrl = String.format("https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed", movie.getId());
        Log.d(TAG, videoUrl);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(videoUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    final String videoId = getFirstYoutubeId(results);
                    Log.d(TAG, String.format("Youtube ID: %s", videoId));

                    // listener launches MovieTrailerActivity when backdrop image is tapped
                    binding.ivBackdrop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class);
                            i.putExtra("video_id", videoId);
                            startActivity(i);
                        }
                    });

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
}