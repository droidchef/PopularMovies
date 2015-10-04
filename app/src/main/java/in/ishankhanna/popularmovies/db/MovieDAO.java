package in.ishankhanna.popularmovies.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import in.ishankhanna.popularmovies.db.DBHelper.MovieEntry;
import in.ishankhanna.popularmovies.models.Movie;


/**
 * Created by ishan on 04/10/15.
 */
public class MovieDAO {

    private static final String TAG = "MovieDAO";
    // Database fields
    private SQLiteDatabase sqLiteDatabase;
    private DBHelper dbHelper;
    private String[] allColumns = { MovieEntry._ID,
            MovieEntry.COLUMN_NAME_ID,
        MovieEntry.COLUMN_NAME_TITLE,
        MovieEntry.COLUMN_NAME_OVERVIEW,
        MovieEntry.COLUMN_NAME_RELEASE_DATE,
        MovieEntry.COLUMN_NAME_POPULARITY,
        MovieEntry.COLUMN_NAME_VOTE_AVERAGE,
        MovieEntry.COLUMN_NAME_POSTER_PATH
    };

    public MovieDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    // Open connection to DB for both reading and writing.
    public void open() throws SQLException {
        sqLiteDatabase = dbHelper.getWritableDatabase();
    }

    // Open connection to DB for reading only.
    public void openForReadOnly() throws SQLException {
        sqLiteDatabase = dbHelper.getReadableDatabase();
    }

    // Close any open connection to DB.
    public void close() throws SQLException {
        dbHelper.close();
    }

    public Movie saveMovie(Movie movie) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieEntry.COLUMN_NAME_ID, movie.getId());
        contentValues.put(MovieEntry.COLUMN_NAME_TITLE, movie.getOriginalTitle());
        contentValues.put(MovieEntry.COLUMN_NAME_OVERVIEW, movie.getOverview());
        contentValues.put(MovieEntry.COLUMN_NAME_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(MovieEntry.COLUMN_NAME_POPULARITY, movie.getPopularity());
        contentValues.put(MovieEntry.COLUMN_NAME_VOTE_AVERAGE, movie.getVoteAverage());
        contentValues.put(MovieEntry.COLUMN_NAME_POSTER_PATH, movie.getPosterPath());

        long insertId = sqLiteDatabase.insertWithOnConflict(MovieEntry.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        if (insertId != -1) {
            Cursor cursor = sqLiteDatabase.query(MovieEntry.TABLE_NAME,
                    allColumns, MovieEntry._ID + " = " + insertId, null,
                    null, null, null);
            cursor.moveToFirst();
            Log.d(TAG, cursor.toString());
            Movie newMovie = cursorToMovie(cursor);
            cursor.close();
            return  newMovie;
        } else {
            return null;
        }
    }

    public List<Movie> getAllMovies() {

        List<Movie> movies = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(MovieEntry.TABLE_NAME, allColumns, null, null, null,null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            Movie movie = cursorToMovie(cursor);
            movies.add(movie);
            cursor.moveToNext();
        }
        cursor.close();
        return movies;
    }

    private Movie cursorToMovie(Cursor cursor) {
        Movie movie = new Movie();
        movie.setId(cursor.getInt(1));
        movie.setOriginalTitle(cursor.getString(2));
        movie.setOverview(cursor.getString(3));
        movie.setReleaseDate(cursor.getString(4));
        movie.setPopularity(cursor.getDouble(5));
        movie.setVoteAverage(cursor.getDouble(6));
        movie.setPosterPath(cursor.getString(7));
        return movie;
    }

}
