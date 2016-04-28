package com.ganesh.popularmovies;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MoviesFragment.Callback {
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private FragmentManager fragmentManager = getSupportFragmentManager();
    MoviesFragment mFragment;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.container_movies) != null) {
            mTwoPane = true;
            mFragment = MoviesFragment.newInstance("","");
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setTwoPane(mTwoPane);
            if (savedInstanceState == null) {
                fragmentManager.beginTransaction()
                        .add(R.id.container, mFragment,MoviesFragment.TAG)
                        .commit();

                fragmentManager.beginTransaction()
                        .add(R.id.container_movies, fragment, MovieDetailFragment.TAG)
                        .commit();
            }else{
                mFragment = (MoviesFragment) fragmentManager.getFragment(
                        savedInstanceState, MoviesFragment.TAG);
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
        if (mFragment != null) {
            mFragment.setTwoPane(mTwoPane);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        fragmentManager.putFragment(savedInstanceState, MoviesFragment.TAG, mFragment);
    }

    @Override
    public void onItemSelected(String[] data,Movie movie) {
        if (mTwoPane) {

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setTwoPane(mTwoPane);
            Bundle args = new Bundle();
            args.putStringArray(Intent.EXTRA_TEXT, data);
            args.putParcelable("movie", movie);
            fragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container_movies, fragment, MovieDetailFragment.TAG);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();

        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
//            intent.putExtra(Intent.EXTRA_TEXT, data);
            intent.putExtra("movie", movie);
            startActivity(intent);
            startActivity(intent);
        }
    }
}
