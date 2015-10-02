package in.ishankhanna.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import in.ishankhanna.popularmovies.models.Movie;
import in.ishankhanna.popularmovies.models.Video;
import in.ishankhanna.popularmovies.models.VideoResponse;
import in.ishankhanna.popularmovies.utils.API;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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
        ratingBar.setRating((float) (movie.getVoteAverage() / 2.00));

        final ListView lvTrailers = (ListView) findViewById(R.id.lv_trailers);

        API.mMoviesService.getTrailersForAMovie(movie.getId(), new Callback<VideoResponse>() {
            @Override
            public void success(final VideoResponse videoResponse, Response response) {

                if (videoResponse != null) {
                    List<String> trailerNamesList = new ArrayList<>();
                    for (Video video : videoResponse.getResults()) {
                        Log.d(TAG, video.getName());
                        trailerNamesList.add(video.getName());
                    }

                    if (trailerNamesList.size() > 0) {
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MovieDetailsActivity.this, android.R.layout.simple_list_item_1, trailerNamesList);
                        lvTrailers.setAdapter(arrayAdapter);

                        lvTrailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                watchYoutubeVideo(videoResponse.getResults().get(position).getKey());

                            }
                        });

                    }
                }

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

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

    /**
     * Starts the Youtube video in the Youtube App or the Web View.
     * @param id
     */
    private void watchYoutubeVideo(String id){
        try{
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            startActivity(intent);
        }catch (ActivityNotFoundException ex){
            Intent intent=new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v="+id));
            startActivity(intent);
        }
    }
}
