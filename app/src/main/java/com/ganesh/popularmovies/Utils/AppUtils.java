package com.ganesh.popularmovies.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.ganesh.popularmovies.MovieDetailFragment;
import com.ganesh.popularmovies.R;
import com.ganesh.popularmovies.synch.MovieDataLoader;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Ganesh on 4/29/2016.
 */
public class AppUtils {

    public static final String API_KEY = "";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w342";
    private static final String PATH_SEPARATOR = "/";

    // Developer Key URL is below
    //https://console.developers.google.com/project/popular-movies-2/apiui/credential?authuser=0#
    public static final String DEVELOPER_KEY = "";
    public static final String YOUTUBE_PACKAGE_NAME = "com.google.android.youtube";
    public static final String YOUTUBE_CLASS_NAME = "com.google.android.youtube.WatchActivity";

    public static String getPreferredSortOrder(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String sortOrder = preferences.getString(context.getString(R.string.pref_sort_order_key),
                context.getString(R.string.pref_sort_order_default));
        return sortOrder;
    }

    @SuppressWarnings("ResourceType")
    public static @MovieDataLoader.MovieStatus int getMovieStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(context.getString(R.string.pref_movie_status_key), MovieDataLoader.MOVIE_STATUS_UNKNOWN);
    }
    public static String getImageURL(String imagePath) {
        StringBuilder imageURL = new StringBuilder();

        imageURL.append(IMAGE_BASE_URL);
        imageURL.append(IMAGE_SIZE);
        imageURL.append(PATH_SEPARATOR);
        imageURL.append(imagePath);

        return imageURL.toString();
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }


    public static String[] loadFavoriteMovieIds (Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> favoritMovieIdsSet =  prefs.getStringSet(MovieDetailFragment.FAVORITE_MOVIE_IDS_SET_KEY, null);
        if (favoritMovieIdsSet != null) {
            String[] array = new String[favoritMovieIdsSet.size()];
            Iterator<String> movieIdsIter = favoritMovieIdsSet.iterator();
            int i = 0;
            while (movieIdsIter.hasNext()) {
                array[i] = movieIdsIter.next();
                i = i + 1;
            }
            return array;
        }
        return null;
    }
    public static boolean isStringEmpty(String string) {
        return (string == null || string.equals("null") || string.equals(""));
    }

    /**
     *
     * This function is for making my list of items to show with individual heights
     * for 2 list views with different heights. And then it is modified
     * according to the MeasureSpec
     * Original Source:
     * http://stackoverflow.com/questions/17693578/android-how-to-display-2-listviews-in-one-activity-one-after-the-other
     * @param mListView
     */
    public static void setDynamicHeight(ListView mListView) {
        ListAdapter mListAdapter = mListView.getAdapter();
        if (mListAdapter == null) {
            // when adapter is null
            return;
        }
        int height = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < mListAdapter.getCount(); i++) {
            View listItem = mListAdapter.getView(i, null, mListView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            height += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount()));
        mListView.setLayoutParams(params);
        mListView.requestLayout();
    }

    public static boolean canResolveIntent(Intent intent, Context context) {
        List<ResolveInfo> resolveInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        return resolveInfo != null && !resolveInfo.isEmpty();
    }
    public static boolean isMovieIdFavorite(String [] favoriteMovieIds, String movieId) {
        boolean result = false;

        if (favoriteMovieIds == null || favoriteMovieIds.length == 0) return result;

        for (int i = 0; i < favoriteMovieIds.length; i++) {
            if (movieId.trim().equals(favoriteMovieIds[i].trim())){
                result = true;
                break;
            }
        }

        return result;
    }
    public static String argsArrayToString(String[] args) {
        StringBuilder argsBuilder = new StringBuilder();

        final int argsCount = args.length;
        for (int i = 0; i < argsCount; i++) {
            argsBuilder.append(args[i]);

            if (i < argsCount - 1) {
                argsBuilder.append(",");
            }
        }

        return argsBuilder.toString();
    }
}
