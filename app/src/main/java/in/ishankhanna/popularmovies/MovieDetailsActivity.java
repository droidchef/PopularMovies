package in.ishankhanna.popularmovies;

import android.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import in.ishankhanna.popularmovies.db.MovieDAO;
import in.ishankhanna.popularmovies.models.Movie;
import in.ishankhanna.popularmovies.models.Review;
import in.ishankhanna.popularmovies.models.ReviewResponse;
import in.ishankhanna.popularmovies.models.Video;
import in.ishankhanna.popularmovies.models.VideoResponse;
import in.ishankhanna.popularmovies.utils.API;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String TAG = "DetailsActivity";
    private VideoResponse videoResponse;
    private ReviewResponse reviewResponse;
    private Movie movie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        movie = getIntent().getParcelableExtra("movie");
        Log.d(TAG, movie.toString());
        ImageView ivMovieThumbnail = (ImageView) findViewById(R.id.iv_movie_thumbnail);
        TextView tvMovieTitle = (TextView) findViewById(R.id.tv_movie_title);
        TextView tvMovieSynopsis = (TextView) findViewById(R.id.tv_movie_synopsis);
        TextView tvReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        TextView tv_rating = (TextView) findViewById(R.id.tv_rating);

        Picasso.with(MovieDetailsActivity.this)
                .load("http://image.tmdb.org/t/p/" + "w185" + movie.getPosterPath())
                .resize(200,300)
                .into(ivMovieThumbnail);

        tvMovieTitle.setText(movie.getOriginalTitle());
        tvMovieSynopsis.setText(movie.getOverview());
        tvReleaseDate.setText("Release Date : " + movie.getReleaseDate());
        try {
            tv_rating.setText(String.valueOf(movie.getVoteAverage()));
        } catch (Exception e) {
            tv_rating.setText("N/A");
        }

        final ListView lvTrailers = (ListView) findViewById(R.id.lv_trailers);
        final List<String> trailerNamesList = new ArrayList<>();
        API.mMoviesService.getTrailersForAMovie(movie.getId(), new Callback<VideoResponse>() {
            @Override
            public void success(final VideoResponse videoResponse, Response response) {

                if (videoResponse != null) {
                    MovieDetailsActivity.this.videoResponse = videoResponse;
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

        final ListView lvReviews = (ListView) findViewById(R.id.lv_reviews);
        final List<String> reviewAuthorList = new ArrayList<>();
        API.mMoviesService.getReviewsForAMovie(movie.getId(), new Callback<ReviewResponse>() {
            @Override
            public void success(final ReviewResponse reviewResponse, Response response) {

                if (reviewResponse != null) {

                    MovieDetailsActivity.this.reviewResponse = reviewResponse;
                    for (Review review : reviewResponse.getReviews()) {
                        reviewAuthorList.add(review.getAuthor());
                    }

                    if (reviewAuthorList.size() > 0) {

                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MovieDetailsActivity.this, android.R.layout.simple_list_item_1, reviewAuthorList);
                        lvReviews.setAdapter(arrayAdapter);

                        lvReviews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                AlertDialog alertDialog = new AlertDialog.Builder(MovieDetailsActivity.this)
                                        .setTitle("Review by " + reviewAuthorList.get(position))
                                        .setMessage(reviewResponse.getReviews().get(position).getContent())
                                        .create();
                                alertDialog.show();

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
        if (id == R.id.action_favorite) {
            MovieDAO movieDAO = new MovieDAO(this);
            movieDAO.open();
            Movie m = movieDAO.saveMovie(movie);
            if (m != null && m.getOriginalTitle().equals(movie.getOriginalTitle())) {
                Toast.makeText(this, "Movie Added to Favorites", Toast.LENGTH_SHORT).show();
            }
            movieDAO.close();
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
