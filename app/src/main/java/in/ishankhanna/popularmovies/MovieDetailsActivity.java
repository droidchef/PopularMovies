package in.ishankhanna.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String TAG = "DetailsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Bundle arguments = new Bundle();
        arguments.putParcelable("movie", getIntent().getParcelableExtra("movie"));
        MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();
        movieDetailsFragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.movie_detail_container, movieDetailsFragment)
                .commit();
    }

}
