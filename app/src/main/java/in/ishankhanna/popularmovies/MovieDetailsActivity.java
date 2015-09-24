package in.ishankhanna.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import in.ishankhanna.popularmovies.models.Movie;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String TAG = "DetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Movie movie = getIntent().getParcelableExtra("movie");
        Log.d(TAG, movie.toString());
        ImageView ivMovieThumbnail = (ImageView) findViewById(R.id.iv_movie_thumbnail);
        TextView tvMovieTitle = (TextView) findViewById(R.id.tv_movie_title);
        TextView tvMovieSynopsis = (TextView) findViewById(R.id.tv_movie_synopsis);
        TextView tvReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        Picasso.with(MovieDetailsActivity.this)
                .load("http://image.tmdb.org/t/p/" + "w780" + movie.getBackdropPath())
                .into(ivMovieThumbnail);

        tvMovieTitle.setText(movie.getOriginalTitle());
        tvMovieSynopsis.setText(movie.getOverview());
        tvReleaseDate.setText("Release Date : " + movie.getReleaseDate());
        ratingBar.setRating((float)(movie.getVoteAverage()/2.00));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_details, menu);
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
