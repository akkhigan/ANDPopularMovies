package com.ganesh.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.ganesh.popularmovies.MoviesFragment;
import com.ganesh.popularmovies.R;
import com.ganesh.popularmovies.utils.AppUtils;
import com.squareup.picasso.Picasso;
/**
 * Created by ganesh on 3/18/2016.
 * This class is to create the Grid of movie posters
 */
public class MoviesAdapter extends CursorAdapter {

    public MoviesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_poster, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String posterPath = cursor.getString(MoviesFragment.COL_POSTER_PATH);
        if (posterPath == null) {
            posterPath = cursor.getString(MoviesFragment.COL_BACK_DROP_PATH);
        }
        Picasso.with(context).load(AppUtils.getImageURL(posterPath)).into(viewHolder.imageView);
    }


    public static class ViewHolder {
        public final ImageView imageView;
        public ViewHolder(View vi) {
            imageView = (ImageView) vi.findViewById(R.id.imgPoster);
        }
    }
}
