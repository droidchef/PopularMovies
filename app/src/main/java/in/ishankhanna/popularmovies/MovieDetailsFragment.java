package in.ishankhanna.popularmovies;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by ishan on 05/10/15.
 */
public class MovieDetailsFragment extends Fragment {

    private static final String TAG = "MovieDetailsFrag";
    private Movie movie;
    private ShareActionProvider shareActionProvider;
    private Intent shareIntent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey("movie")) {
            movie = getArguments().getParcelable("movie");
        }

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        Log.d(TAG, movie.toString());
        ImageView ivMovieThumbnail = (ImageView) rootView.findViewById(R.id.iv_movie_thumbnail);
        TextView tvMovieTitle = (TextView) rootView.findViewById(R.id.tv_movie_title);
        TextView tvMovieSynopsis = (TextView) rootView.findViewById(R.id.tv_movie_synopsis);
        TextView tvReleaseDate = (TextView) rootView.findViewById(R.id.tv_release_date);
        TextView tv_rating = (TextView) rootView.findViewById(R.id.tv_rating);

        Picasso.with(getActivity())
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

        final ListView lvTrailers = (ListView) rootView.findViewById(R.id.lv_trailers);
        final List<String> trailerNamesList = new ArrayList<>();
        enableNestedScrolling(lvTrailers);
        API.mMoviesService.getTrailersForAMovie(movie.getId(), new Callback<VideoResponse>() {
            @Override
            public void success(final VideoResponse videoResponse, Response response) {

                if (videoResponse != null) {

                    if (videoResponse.getResults().size() > 0) {
                        shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "http://www.youtube.com/watch?v=" + videoResponse.getResults().get(0).getKey());
                        shareIntent.setType("text/plain");
                        setShareIntent(shareIntent);
                    }

                    for (Video video : videoResponse.getResults()) {
                        Log.d(TAG, video.getName());
                        trailerNamesList.add(video.getName());
                    }

                    if (trailerNamesList.size() > 0) {
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, trailerNamesList);
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

        final ListView lvReviews = (ListView) rootView.findViewById(R.id.lv_reviews);
        final List<String> reviewAuthorList = new ArrayList<>();
        enableNestedScrolling(lvReviews);
        API.mMoviesService.getReviewsForAMovie(movie.getId(), new Callback<ReviewResponse>() {
            @Override
            public void success(final ReviewResponse reviewResponse, Response response) {

                if (reviewResponse != null) {

                    for (Review review : reviewResponse.getReviews()) {
                        reviewAuthorList.add(review.getAuthor());
                    }

                    if (reviewAuthorList.size() > 0) {

                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, reviewAuthorList);
                        lvReviews.setAdapter(arrayAdapter);

                        lvReviews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
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

        return rootView;
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

    private void enableNestedScrolling(ListView listView) {

        listView.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

    }

    private void setShareIntent(Intent shareIntent) {
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public void setHasOptionsMenu(boolean hasMenu) {
        super.setHasOptionsMenu(hasMenu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_movie_details, menu);

        // Fetch and store ShareActionProvider
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menu.findItem(R.id.menu_item_share));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favorite) {
            MovieDAO movieDAO = new MovieDAO(getActivity());
            movieDAO.open();
            Movie m = movieDAO.saveMovie(movie);
            if (m != null && m.getOriginalTitle().equals(movie.getOriginalTitle())) {
                Toast.makeText(getActivity(), "Movie Added to Favorites", Toast.LENGTH_SHORT).show();
            }
            movieDAO.close();
        } else if(id == R.id.menu_item_share) {
            Log.d(TAG, "Test");
            startActivity(shareIntent);
        }

        return super.onOptionsItemSelected(item);
    }
}
