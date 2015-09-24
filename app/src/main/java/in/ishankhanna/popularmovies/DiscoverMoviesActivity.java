package in.ishankhanna.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.Collections;
import java.util.List;

import in.ishankhanna.popularmovies.adapters.MovieTilesAdapter;
import in.ishankhanna.popularmovies.models.Movie;
import in.ishankhanna.popularmovies.models.MovieDbResponse;
import in.ishankhanna.popularmovies.utils.API;
import in.ishankhanna.popularmovies.utils.comparators.MoviePopularityComparator;
import in.ishankhanna.popularmovies.utils.comparators.MovieRatingComparator;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DiscoverMoviesActivity extends AppCompatActivity {
    private final String TAG = "DiscoverMoviesActivity";
    private final static int SORT_BY_RATING = 1;
    private final static int SORT_BY_POPULARITY = 2;


    GridView moviesGridView;
    MovieTilesAdapter movieTilesAdapter;
    List<Movie> movies;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_movies);

        moviesGridView = (GridView) findViewById(R.id.gridViewMovies);

        inflateMoviesGridByDataFromNetwork("popularity.desc");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_discover_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_sort_by_popularity : sortMoviesGrid(SORT_BY_POPULARITY);
                break;
            case R.id.action_sort_by_rating: sortMoviesGrid(SORT_BY_RATING);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sortMoviesByRating() {
        Collections.sort(movies, new MovieRatingComparator());
    }
    private void sortMoviesByPopularity() {
        Collections.sort(movies, new MoviePopularityComparator());
    }

    private void sortMoviesGrid(int sortBy) {

        switch(sortBy) {
            case SORT_BY_POPULARITY: inflateMoviesGridByDataFromNetwork("popularity.desc");
                break;
            case SORT_BY_RATING: inflateMoviesGridByDataFromNetwork("vote_average.desc");
                break;
        }

    }

    private void inflateMoviesGridByDataFromNetwork(String sortBy) {

        // pixels, dpi
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int heightPixels = metrics.heightPixels;
        final int widthPixels = metrics.widthPixels;


        API.mMoviesService.getLatestsMoviesInDecreasingOrderOfPopularity(sortBy, new Callback<MovieDbResponse>() {
            @Override
            public void success(MovieDbResponse movieDbResponse, Response response) {

                Log.d(TAG, "Movies Fetched : " + movieDbResponse.getMovies().size());
                movies = movieDbResponse.getMovies();
                movieTilesAdapter = new MovieTilesAdapter(getApplicationContext(), movies, widthPixels);
                moviesGridView.setAdapter(movieTilesAdapter);
                moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d(TAG, movies.get(position).toString());

                        Intent detailsActivityIntent = new Intent(DiscoverMoviesActivity.this, MovieDetailsActivity.class);
                        detailsActivityIntent.putExtra("movie", movies.get(position));
                        startActivity(detailsActivityIntent);

                    }
                });

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
