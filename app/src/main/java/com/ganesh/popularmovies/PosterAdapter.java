package com.ganesh.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ganesh on 3/18/2016.
 * This class is to create the Grid of movie posters
 */
public class PosterAdapter extends ArrayAdapter<String> {

    private LayoutInflater mLayoutInflater;
    private Context context;
    private int layoutId;
    private int imageViewID;

    public PosterAdapter(Context context, int layoutId, int imageViewID, ArrayList<String> urls) {
        super(context, 0, urls);
        this.mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.layoutId = layoutId;
        this.imageViewID = imageViewID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        String url;
        if (v == null) {
            v = mLayoutInflater.inflate(layoutId, parent, false);
        }
        ImageView imageView = (ImageView) v.findViewById(imageViewID);
        url = getItem(position);
        Picasso.with(context).load(url).into(imageView);
        return v;
    }
}
