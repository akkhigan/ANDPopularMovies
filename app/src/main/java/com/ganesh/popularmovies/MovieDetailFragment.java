package com.ganesh.popularmovies;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailFragment extends Fragment {

    private Movie movie;
    public MovieDetailFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("movie")) {
            movie = (Movie) intent.getParcelableExtra("movie");
            initViews(view);
        }
        return view;
    }
    private void initViews(View v){
        TextView title = (TextView) v.findViewById(R.id.txtTitle);
        ImageView poster = (ImageView) v.findViewById(R.id.imgPoster);
        TextView releaseDate = (TextView) v.findViewById(R.id.txtReleaseDate);
        TextView ratings = (TextView) v.findViewById(R.id.txtRatings);
        TextView overview = (TextView) v.findViewById(R.id.txtSynopsis);

        title.setText(movie.getTitle());
        Picasso.with(getActivity()).load(movie.getPoster()).into(poster);
        releaseDate.setText(movie.getReleaseDate());
        ratings.setText(movie.getVoteAverage() + "/10");
        overview.setText(movie.getOverview());
    }
}
