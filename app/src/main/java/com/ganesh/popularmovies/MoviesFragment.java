package com.ganesh.popularmovies;


import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.ganesh.popularmovies.adapters.MoviesAdapter;
import com.ganesh.popularmovies.data.PopularMoviesContract;
import com.ganesh.popularmovies.data.PopularMoviesContract.MovieEntry;
import com.ganesh.popularmovies.synch.MovieDataLoader;
import com.ganesh.popularmovies.synch.PopularMoviesSyncAdapter;
import com.ganesh.popularmovies.utils.AppUtils;

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
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    public static final String TAG = MoviesFragment.class.getSimpleName();

    private static final int FORECAST_LOADER = 0;
    private MoviesAdapter mMoviesAdapter;
    private String sortOrder;
    private ArrayList<Movie> movies = new ArrayList<Movie>();
    private boolean mTwoPane;
    public static final String[] MOVIE_COLUMNS = {
            MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
            MovieEntry.COLUMN_MOVIE_ID,
            MovieEntry.COLUMN_IS_ADULT,
            MovieEntry.COLUMN_BACK_DROP_PATH,
            MovieEntry.COLUMN_ORIGINAL_LANGUAGE,
            MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieEntry.COLUMN_OVERVIEW,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_POSTER_PATH,
            MovieEntry.COLUMN_POPULARITY,
            MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_IS_VIDEO,
            MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieEntry.COLUMN_VOTE_COUNT,
            MovieEntry.COLUMN_RUNTIME,
            MovieEntry.COLUMN_STATUS,
            MovieEntry.COLUMN_DATE
    };

    public static final int COL_MOVIE_PK_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_IS_ADULT = 2;
    public static final int COL_BACK_DROP_PATH = 3;
    public static final int COL_ORIGINAL_LANGUAGE = 4;
    public static final int COL_ORIGINAL_TITLE = 5;
    public static final int COL_OVERVIEW = 6;
    public static final int COL_RELEASE_DATE = 7;
    public static final int COL_POSTER_PATH = 8;
    public static final int COL_POPULARITY = 9;
    public static final int COL_TITLE = 10;
    public static final int COL_IS_VIDEO = 11;
    public static final int COL_VOTE_AVERAGE = 12;
    public static final int COL_VOTE_COUNT = 13;
    public static final int COL_RUNTIME = 14;
    public static final int COL_STATUS = 15;
    public static final int COL_DATE = 16;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movies, container, false);
        GridView gridView = (GridView) view.findViewById(R.id.gridMovies);
        TextView emptyTextView = (TextView) view.findViewById(R.id.txtEmptyView);
        gridView.setEmptyView(emptyTextView);
        emptyTextView.setText(getString(R.string.label_no_favorite_movies));
        mMoviesAdapter = new MoviesAdapter(getActivity(), null, 0);
        gridView.setAdapter(mMoviesAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                String backDropPath = cursor.getString(COL_BACK_DROP_PATH);
                String posterPath = cursor.getString(COL_POSTER_PATH);
                if (backDropPath == null || backDropPath.equals("null")) {
                    if (posterPath != null && !posterPath.equals("null")) {
                        backDropPath = posterPath;
                    }
                }
                if (posterPath == null || posterPath.equals("null")) {
                    if (backDropPath != null && !backDropPath.equals("null")) {
                        posterPath = backDropPath;
                    }
                }
                String data[] = {backDropPath, posterPath, cursor.getString(COL_RELEASE_DATE).toString(),
                        Double.toString(cursor.getDouble(COL_RUNTIME)), Double.toString(cursor.getDouble(COL_VOTE_AVERAGE)), cursor.getString(COL_OVERVIEW),
                        cursor.getString(COL_ORIGINAL_TITLE), Integer.toString(cursor.getInt(COL_MOVIE_ID)), Boolean.toString(mTwoPane)};
                ((Callback) getActivity()).onItemSelected(data);
            }

        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void onSortOrderChanged() {
        PopularMoviesSyncAdapter.syncImmediately(getActivity());
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
    }

    public void setTwoPane(boolean mTwoPane) {
        this.mTwoPane = mTwoPane;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrderSelected = prefs.getString(getActivity().getString(R.string.pref_sort_order_key), null);
        String sortOrder = PopularMoviesContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        if (sortOrderSelected != null && sortOrderSelected.equals(getActivity().getString(R.string.pref_sort_order_vote_average))) {
            sortOrder = MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
        }
        Uri weatherForLocationUri = PopularMoviesContract.MovieEntry.buildMovieUri();
        return new CursorLoader(getActivity(),
                weatherForLocationUri,
                MOVIE_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mMoviesAdapter.swapCursor(cursor);
        updateEmptyView();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdapter.swapCursor(null);
    }

    public interface Callback {
        public void onItemSelected(String data[]);
    }

    private void updateEmptyView() {
        if (mMoviesAdapter.getCount() == 0) {
            TextView emptyTextView = (TextView) getView().findViewById(R.id.txtEmptyView);
            if (null != emptyTextView) {
                int message = R.string.empty_movies_list;
                @MovieDataLoader.MovieStatus int status = AppUtils.getMovieStatus(getActivity());
                switch (status) {
                    case MovieDataLoader.MOVIE_STATUS_SERVER_DOWN:
                        message = R.string.empty_movies_list_server_down;
                        break;
                    case MovieDataLoader.MOVIE_STATUS_SERVER_INVALID:
                        message = R.string.empty_movies_list_server_error;
                        break;
                    default:
                        String[] favoriteMovieIds = AppUtils.loadFavoriteMovieIds(getActivity());
                        if ((favoriteMovieIds == null || favoriteMovieIds.length <= 0) &&
                                AppUtils.getPreferredSortOrder(getActivity()).equals(getActivity().getString(R.string.pref_sort_order_favorite))) {
                            message = R.string.label_no_favorite_movies;
                        } else if (!AppUtils.isNetworkConnected(getActivity())) {
                            message = R.string.empty_movies_list_no_network;
                        }
                        break;
                }
                emptyTextView.setText(message);
            }
        }
    }


}
