package com.ganesh.popularmovies;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MoviesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoviesFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    public static final String TAG = MoviesFragment.class.getSimpleName();
    private final String STORED_MOVIES = "stored_movies";
    private SharedPreferences preferences;
    private PosterAdapter mPosterAdapter;
    private String sortOrder;
    private ArrayList<Movie> movies = new ArrayList<Movie>();
    private boolean mTwoPane;

    public MoviesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MoviesFragment.
     */
    public static MoviesFragment newInstance(String param1, String param2) {
        MoviesFragment fragment = new MoviesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sortOrder = preferences.getString(getString(R.string.sort_order_key),
                getString(R.string.sort_default_value));

        if (savedInstanceState != null) {
            ArrayList<Movie> storedMovies = new ArrayList<Movie>();
            storedMovies = savedInstanceState.<Movie>getParcelableArrayList(STORED_MOVIES);
            movies.clear();
            movies.addAll(storedMovies);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movies, container, false);
        GridView gridView = (GridView) view.findViewById(R.id.gridMovies);
        mPosterAdapter = new PosterAdapter(
                getActivity(),
                R.layout.item_poster,
                R.id.imgPoster,
                new ArrayList<String>());
        gridView.setAdapter(mPosterAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movie movie = movies.get(position);
                ((Callback) getActivity()).onItemSelected(null,movie);
            }

        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // checking that sort order has changed recently
        String prefSortOrder = preferences.getString(getString(R.string.sort_order_key),
                getString(R.string.sort_default_value));
        if (movies.size() > 0 && prefSortOrder.equals(sortOrder)) {
            updatePosterAdapter();
        } else {
            sortOrder = prefSortOrder;
            getMovies();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Movie> storedMovies = new ArrayList<Movie>();
        storedMovies.addAll(movies);
        outState.putParcelableArrayList(STORED_MOVIES, storedMovies);
    }
    public void setTwoPane(boolean mTwoPane) {
        this.mTwoPane = mTwoPane;
    }
    private void getMovies() {
        TheMoviesDBTask fetchMoviesTask = new TheMoviesDBTask();
        fetchMoviesTask.execute(sortOrder);
    }

    // updates the ArrayAdapter of poster images
    private void updatePosterAdapter() {
        mPosterAdapter.clear();
        for (Movie movie : movies) {
            mPosterAdapter.add(movie.getPoster());
        }
    }

    private class TheMoviesDBTask extends AsyncTask<String, Void, List<Movie>> {
        private final String LOG_TAG = TheMoviesDBTask.class.getSimpleName();
        private final String API_KEY = "28aa8ca5f8398dbe93c3755f76cbc4ec";
        private final String MOVIE_POSTER_BASE = "http://image.tmdb.org/t/p/";
        private final String MOVIE_POSTER_SIZE = "w185";

        @Override
        protected List<Movie> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;

            try {

                final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_BY = "sort_by";
                final String KEY = "api_key";
                String sortBy = params[0];

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY, sortBy)
                        .appendQueryParameter(KEY, API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return parseData(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        private List<Movie> parseData(String moviesJsonStr) throws JSONException {

            // parsing json properties
            final String ARRAY_OF_MOVIES = "results";
            final String ORIGINAL_TITLE = "original_title";
            final String POSTER_PATH = "poster_path";
            final String OVERVIEW = "overview";
            final String VOTE_AVERAGE = "vote_average";
            final String RELEASE_DATE = "release_date";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(ARRAY_OF_MOVIES);
            int moviesLength = moviesArray.length();
            List<Movie> movies = new ArrayList<Movie>();

            for (int i = 0; i < moviesLength; ++i) {
                JSONObject movie = moviesArray.getJSONObject(i);
                String title = movie.getString(ORIGINAL_TITLE);
                String poster = MOVIE_POSTER_BASE + MOVIE_POSTER_SIZE + movie.getString(POSTER_PATH);
                String overview = movie.getString(OVERVIEW);
                String voteAverage = movie.getString(VOTE_AVERAGE);
                String releaseDate = getYear(movie.getString(RELEASE_DATE));

                movies.add(new Movie(title, poster, overview, voteAverage, releaseDate));

            }
            return movies;

        }

        private String getYear(String date) {
            final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            final Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(df.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return Integer.toString(cal.get(Calendar.YEAR));
        }

        @Override
        protected void onPostExecute(List<Movie> results) {
            super.onPostExecute(movies);
            if (results != null) {
                movies.clear();
                movies.addAll(results);
                updatePosterAdapter();
            } else {
                Log.e(LOG_TAG, "No data");
            }

        }
    }
    public interface Callback {
        public void onItemSelected(String data[],Movie movie);
    }
}
