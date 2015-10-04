package in.ishankhanna.popularmovies.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by ishan on 04/10/15.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String COLUMN_TYPE_INTEGER = " INTEGER ";
    private static final String COLUMN_TYPE_REAL = " REAL ";
    private static final String COLUMN_TYPE_TEXT = " TEXT ";
    private static final String COMMA = " , ";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE "+ MovieEntry.TABLE_NAME + "(" +
                    MovieEntry._ID + " INTEGER PRIMARY KEY " + COMMA +
                    MovieEntry.COLUMN_NAME_ID + COLUMN_TYPE_INTEGER + " UNIQUE "+ COMMA +
                    MovieEntry.COLUMN_NAME_TITLE + COLUMN_TYPE_TEXT + COMMA +
                    MovieEntry.COLUMN_NAME_OVERVIEW + COLUMN_TYPE_TEXT + COMMA +
                    MovieEntry.COLUMN_NAME_RELEASE_DATE + COLUMN_TYPE_TEXT + COMMA +
                    MovieEntry.COLUMN_NAME_POPULARITY + COLUMN_TYPE_REAL + COMMA +
                    MovieEntry.COLUMN_NAME_VOTE_AVERAGE + COLUMN_TYPE_REAL + COMMA +
                    MovieEntry.COLUMN_NAME_POSTER_PATH + COLUMN_TYPE_TEXT +
                    ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PopularMovies.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /* Inner class that defines the table contents */
    public static abstract class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorite_movies";
        public static final String COLUMN_NAME_ID = "movie_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_OVERVIEW = "overview";
        public static final String COLUMN_NAME_RELEASE_DATE = "release_date";
        public static final String COLUMN_NAME_POPULARITY = "popularity";
        public static final String COLUMN_NAME_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_NAME_POSTER_PATH = "poster_path";

    }

}
