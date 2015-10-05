package in.ishankhanna.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import in.ishankhanna.popularmovies.models.Movie;

public class DiscoverMoviesActivity extends AppCompatActivity implements MoviesGridFragment.MovieSelectionListener{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_movies);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
            Log.d("DiscoverMoviesAct", "Two Panes");



        }
    }


    @Override
    public void onMovieSelected(Movie movie) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putParcelable("movie", movie);
            MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();
            movieDetailsFragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, movieDetailsFragment)
                    .commit();
        } else {

            // In single-pane mode, simply start the detail activity
            // for the selected movie.
            Intent detailsActivityIntent = new Intent(DiscoverMoviesActivity.this, MovieDetailsActivity.class);
            detailsActivityIntent.putExtra("movie", movie);
            startActivity(detailsActivityIntent);


        }
    }
}
