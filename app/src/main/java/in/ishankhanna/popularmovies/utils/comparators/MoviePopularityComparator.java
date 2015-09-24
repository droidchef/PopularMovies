package in.ishankhanna.popularmovies.utils.comparators;

import java.util.Comparator;

import in.ishankhanna.popularmovies.models.Movie;

/**
 * Created by ishan on 24/09/15.
 */
public class MoviePopularityComparator implements Comparator<Movie> {

    @Override
    public int compare(Movie lhs, Movie rhs) {
        return lhs.getPopularity() >= rhs.getPopularity() ? -1 : 1;
    }
}
