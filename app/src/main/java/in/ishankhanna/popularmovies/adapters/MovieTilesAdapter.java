package in.ishankhanna.popularmovies.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import in.ishankhanna.popularmovies.models.Result;

/**
 * Created by ishan on 23/09/15.
 */
public class MovieTilesAdapter extends BaseAdapter {

    private Context mContext;
    private List<Result> moviesList = new ArrayList<>();
    private int widthPixels;
    public MovieTilesAdapter(Context context, List<Result> moviesList, int widthPixels) {
        this.mContext = context;
        this.moviesList = moviesList;
        this.widthPixels = widthPixels;
    }


    @Override
    public int getCount() {
        return moviesList.size();
    }

    @Override
    public Object getItem(int position) {
        return moviesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            int width = widthPixels / 2;
            int height = (int) (width * 1.2);
            imageView.setLayoutParams(new GridView.LayoutParams(width, height));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(mContext)
                .load("http://image.tmdb.org/t/p/" + "w780" + ((Result)getItem(position)).getBackdropPath())
                        .into(imageView);
        return imageView;


    }
}
