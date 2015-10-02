package in.ishankhanna.popularmovies.utils;

import in.ishankhanna.popularmovies.BuildConfig;
import in.ishankhanna.popularmovies.models.MovieDbResponse;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by ishan on 23/09/15.
 */
public class API {

    private static final String YOUR_API_KEY = "ADD YOUR API KEY HERE";

    /**
     * Retrofit Log Level to be used by the rest adapter while making requests.
     */
    private static RestAdapter.LogLevel LOG_LEVEL = RestAdapter.LogLevel.NONE;

    /**
     * The main instance URL to which all the requests for Movies will be made.
     */
    private static final String MOVIES_DB_INSTANCE_URL = "http://api.themoviedb.org/3";

    /**
     * MoviesService Interface
     */
    public static MoviesService mMoviesService;

    /**
     * The RestAdapter that powers the MoviesDb API for Movies
     */
    private static RestAdapter mRestAdapter;

    static {
        initAdapter();
    }

    /**
     * Initialises the MoviesDbApi by creating the RestAdapters for Movies
     */
    private static synchronized void initAdapter() {
        // Network Request Logging is only required in the Debug Mode
        if (BuildConfig.DEBUG) {
            LOG_LEVEL = RestAdapter.LogLevel.FULL;
        } else {
            LOG_LEVEL = RestAdapter.LogLevel.NONE;
        }
        mRestAdapter = createRestAdapter(MOVIES_DB_INSTANCE_URL);

        initServices();
    }

    /**
     * Plugs in all the required Services for the Network Communication
     */
    private static synchronized void initServices() {
        mMoviesService = mRestAdapter.create(MoviesService.class);
    }

    /**
     * Initialises a RestAdapter with an Instance URL and a Log Level
     *
     * @return restAdapter RestAdapter
     */
    private static RestAdapter createRestAdapter(final String URL) {


        return new RestAdapter.Builder()
                .setEndpoint(URL)
                .setLogLevel(LOG_LEVEL)
                .build();
    }

    /**
     * Helper method to easily toggle the RestAdapter Logging inline.
     *
     * @param logLevel Level of Logging required by the RestAdapter
     */
    public static void changeLogLevelOfRestAdapter(RestAdapter.LogLevel logLevel) {
        mRestAdapter.setLogLevel(logLevel);
        /*
         Because the RestAdapter is static, we must reinitialise the services that
         were created by this adapter otherwise the changes won't reflect.
        */
        initServices();
    }

    public static RestAdapter getmRestAdapter() {
        return mRestAdapter;
    }

    /**
     * Movies Service will provide interface for communicating with the The Movies Db API
     */
    public interface MoviesService {

        @GET("/discover/movie?api_key=" + YOUR_API_KEY)
        void getLatestMoviesInDecreasingOrderOfPopularity(@Query("sort_by") String sortBy, Callback<MovieDbResponse> movieDbResponseCallback);

    }



}
