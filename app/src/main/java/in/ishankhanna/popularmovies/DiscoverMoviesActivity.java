package in.ishankhanna.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import in.ishankhanna.popularmovies.adapters.MovieTilesAdapter;
import in.ishankhanna.popularmovies.models.MovieDbResponse;
import in.ishankhanna.popularmovies.utils.API;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DiscoverMoviesActivity extends AppCompatActivity {
    private final String TAG = "DiscoverMoviesActivity";

    GridView moviesGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_movies);

        // pixels, dpi
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int heightPixels = metrics.heightPixels;
        final int widthPixels = metrics.widthPixels;

        moviesGridView = (GridView) findViewById(R.id.gridViewMovies);

        API.mMoviesService.getLatestsMoviesInDecreasingOrderOfPopularity(new Callback<MovieDbResponse>() {
            @Override
            public void success(MovieDbResponse movieDbResponse, Response response) {

                Log.d(TAG, "Movies Fetched : " + movieDbResponse.getResults().size());

                MovieTilesAdapter movieTilesAdapter = new MovieTilesAdapter(getApplicationContext(), movieDbResponse.getResults(), widthPixels);
                moviesGridView.setAdapter(movieTilesAdapter);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
