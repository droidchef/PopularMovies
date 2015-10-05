package in.ishankhanna.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.List;

import in.ishankhanna.popularmovies.adapters.MovieTilesAdapter;
import in.ishankhanna.popularmovies.db.DBHelper;
import in.ishankhanna.popularmovies.db.MovieDAO;
import in.ishankhanna.popularmovies.models.Movie;
import in.ishankhanna.popularmovies.models.MovieResponse;
import in.ishankhanna.popularmovies.providers.MovieContentProvider;
import in.ishankhanna.popularmovies.utils.API;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ishan on 05/10/15.
 */
public class MoviesGridFragment extends Fragment {

    private static final String KEY_LAST_SORT_BY = "last_sort_by";
    private static final String KEY_LAST_GRID_POSITION = "last_grid_position";
    private final String TAG = "DiscoverMoviesActivity";
    private final static int SORT_BY_RATING = 1;
    private final static int SORT_BY_POPULARITY = 2;
    private final static int SORT_BY_FAVORITES = 3;

    private final static String SORT_STRING_POPULARITY = "popularity.desc";
    private final static String SORT_STRING_VOTE_AVERAGE = "vote_average.desc";

    GridView moviesGridView;
    MovieTilesAdapter movieTilesAdapter;
    List<Movie> movies;
    private int currentlySortingBy = -1;
    private Context context;

    private MovieSelectionListener mMovieSelectionListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movies_grid, container, false);

        context = getActivity();
        
        moviesGridView = (GridView) rootView.findViewById(R.id.gridViewMovies);

        currentlySortingBy = PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY_LAST_SORT_BY, SORT_BY_POPULARITY);

        sortMoviesGrid(currentlySortingBy);

        return rootView;
    }

    private void sortMoviesGrid(int sortBy) {
        currentlySortingBy = sortBy;
        switch(sortBy) {
            case SORT_BY_POPULARITY: inflateMoviesGridByDataFromNetwork(SORT_STRING_POPULARITY);
                break;
            case SORT_BY_RATING: inflateMoviesGridByDataFromNetwork(SORT_STRING_VOTE_AVERAGE);
                break;
            case SORT_BY_FAVORITES: // inflateMoviesGridByDataFromDatabase();
                inflateMoviesGridByDataFromContentProviders();
                break;
        }

    }

    private void inflateMoviesGridByDataFromNetwork(String sortBy) {

        // pixels, dpi
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final int widthPixels = metrics.widthPixels;

        API.mMoviesService.getLatestMovies(sortBy, new Callback<MovieResponse>() {
            @Override
            public void success(MovieResponse movieDbResponse, Response response) {

                Log.d(TAG, "Movies Fetched : " + movieDbResponse.getMovies().size());
                movies = movieDbResponse.getMovies();
                movieTilesAdapter = new MovieTilesAdapter(context, movies, widthPixels);
                moviesGridView.setAdapter(movieTilesAdapter);
                setMovieGridItemClickListener();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    /**
     * Since I want to Exceed the expectations of the reviewer,
     * I must use Content Providers to Populate the Favorite Movies :)
     */
    @Deprecated
    private void inflateMoviesGridByDataFromDatabase() {

        // pixels, dpi
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final int widthPixels = metrics.widthPixels;
        MovieDAO movieDAO = new MovieDAO(context);
        movieDAO.openForReadOnly();
        movies = movieDAO.getAllMovies();
        movieDAO.close();
        Log.d(TAG, "" + movies.size());
        movieTilesAdapter = new MovieTilesAdapter(context, movies, widthPixels);
        moviesGridView.setAdapter(movieTilesAdapter);
        setMovieGridItemClickListener();

    }

    private void inflateMoviesGridByDataFromContentProviders() {

        // pixels, dpi
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final int widthPixels = metrics.widthPixels;
        String[] projection = { DBHelper.MovieEntry._ID,
                DBHelper.MovieEntry.COLUMN_NAME_ID,
                DBHelper.MovieEntry.COLUMN_NAME_TITLE,
                DBHelper.MovieEntry.COLUMN_NAME_OVERVIEW,
                DBHelper.MovieEntry.COLUMN_NAME_RELEASE_DATE,
                DBHelper.MovieEntry.COLUMN_NAME_POPULARITY,
                DBHelper.MovieEntry.COLUMN_NAME_VOTE_AVERAGE,
                DBHelper.MovieEntry.COLUMN_NAME_POSTER_PATH
        };
        Cursor cursor = context.getContentResolver().query(MovieContentProvider.CONTENT_URI, projection, null, null, null);

        movies = MovieDAO.getAllMovies(cursor);

        Log.d(TAG, "" + movies.size());
        movieTilesAdapter = new MovieTilesAdapter(context, movies, widthPixels);
        moviesGridView.setAdapter(movieTilesAdapter);
        setMovieGridItemClickListener();

    }

    private void setMovieGridItemClickListener() {
        moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, movies.get(position).toString());
                moviesGridView.setItemChecked(position, true);
                mMovieSelectionListener.onMovieSelected(movies.get(position));

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_LAST_SORT_BY, currentlySortingBy);
        outState.putInt(KEY_LAST_GRID_POSITION, moviesGridView.getFirstVisiblePosition());
    }

    @Override
    public void onPause() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_LAST_SORT_BY, currentlySortingBy);
        editor.apply();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        currentlySortingBy = sharedPreferences.getInt(KEY_LAST_SORT_BY, SORT_BY_POPULARITY);
    }

    @Override
    public void setHasOptionsMenu(boolean hasMenu) {
        super.setHasOptionsMenu(hasMenu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_discover_movies, menu);
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
            case R.id.action_show_favorites: sortMoviesGrid(SORT_BY_FAVORITES);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mMovieSelectionListener = (MovieSelectionListener) context;
        } catch (ClassCastException e) {
            throw new RuntimeException("Activity must implement this interface");
        }
    }

    public interface MovieSelectionListener {
        void onMovieSelected(Movie movie);
    }


}
