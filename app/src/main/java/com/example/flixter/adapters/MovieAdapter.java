package com.example.flixter.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flixter.MovieDetailsActivity;
import com.example.flixter.MovieTrailerActivity;
import com.example.flixter.R;
import com.example.flixter.databinding.ItemMovieBinding;
import com.example.flixter.models.Movie;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    Context context;
    List<Movie> movies;

    public MovieAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    // usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("MovieAdapter", "onCreateViewHolder");
        return new ViewHolder(ItemMovieBinding.inflate(LayoutInflater.from(context)));
    }

    // involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("MovieAdapter", "onBindViewHolder" + position);
        // get the movie at the position
        Movie movie = movies.get(position);
        // bind the movie data into the viewholder
        holder.bind(movie);

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ItemMovieBinding binding;

        public ViewHolder(@NonNull ItemMovieBinding b) {
            super(b.getRoot());

            binding = b;
            itemView.setOnClickListener(this);

        }

        public void bind(Movie movie) {
            binding.tvTitle.setText(movie.getTitle());
            binding.tvOverview.setText(movie.getOverview());
            String imageUrl;
            Resources res = context.getResources();
            Drawable placeholder;

            // if phone is in landscape
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                imageUrl = movie.getBackdropPath();
                placeholder = ResourcesCompat.getDrawable(res, R.drawable.flicks_backdrop_placeholder, null);
            } else {
                // else imageUrl = poster image
                imageUrl = movie.getPosterPath();
                placeholder = ResourcesCompat.getDrawable(res, R.drawable.flicks_movie_placeholder, null);
            }
            int radius = 20;
            int margin = 5;
            Glide
                    .with(context)
                    .load(imageUrl)
                    .transform(new RoundedCornersTransformation(radius, margin))
                    .placeholder(placeholder)
                    .into(binding.ivPoster);
        }

        @Override
        // when the user clicks on a row, show MovieDetailsActivity for the selected movie
        public void onClick(View view) {
            // get item position
            int position = getAdapterPosition();
            // make sure the position is valid
            if (position != RecyclerView.NO_POSITION) {
                // get the movie at the position in the list
                Movie movie = movies.get(position);
                // create an intent to display MovieDetailsActivity
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                // Pass the movie as an extra serializable via Parcels.wrap()
                intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));
                // Show the activity
                context.startActivity(intent);
            }
        }

    }

}
