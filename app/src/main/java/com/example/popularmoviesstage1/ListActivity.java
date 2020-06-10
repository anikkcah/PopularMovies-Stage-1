package com.example.popularmoviesstage1;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.popularmoviesstage1.data.Constants;
import com.example.popularmoviesstage1.data.Media;
import com.example.popularmoviesstage1.utilities.GridAutofitLayoutManager;
import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.popularmoviesstage1.R.drawable.movie_placeholder;

import com.example.popularmoviesstage1.utilities.NetworkUtil;

/**
 * An activity representing a list of Media. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link DetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ListActivity extends AppCompatActivity {


    private static final String TAG_NAME = ListActivity.class.getSimpleName();

    AppBarLayout appBarLayout;
    boolean isCollapsed = false;

    //public Context ctx;


    private MediumListRecyclerViewAdapter mMovieAdapter = null;
    private ArrayList<Media> mMediaInformation = null;
    private String mSortOrder = null;
    private Toolbar toolbar;

    public ImageView network_error;


    private String mScreenDensity = null;
    private RecyclerView myThumbnailView;


    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medium_list);

        network_error = (ImageView) findViewById(R.id.network_error_image);
        network_error.setVisibility(View.VISIBLE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());


        mSortOrder = getResources().getString(R.string.media_popular);

        viewCreationCycle();
    }

    @Override
    protected void onResume(){
        super.onResume();

        //toolbar.setTitle("Popular Movies");
        //toolbar.inflateMenu(R.menu.settings_menu);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener(){

            @Override
            public boolean onMenuItemClick(MenuItem item){

                int id = item.getItemId();

                if (id == R.id.sort_results) {
                    sortHelper(item);
                    return true;
                }

                return ListActivity.super.onOptionsItemSelected(item);

            }
        });

       // mSortOrder = getResources().getString(R.string.media_popular);

        viewCreationCycle();
    }



    private void viewCreationCycle(){

        if (mMediaInformation == null) {
            mMediaInformation = new ArrayList<>();
        }


        View recyclerView = findViewById(R.id.medium_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.medium_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }


        int type = NetworkUtil.getConnectionType(getApplicationContext());


        if (type == 1 || type == 2) {

            network_error.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            getScreenDensity();

            onRequestMovieAPI();

        } else {
            // NOT connected...

            network_error.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);



            Toast.makeText(getApplicationContext(), getResources().getString(R.string.media_network_connection), Toast.LENGTH_LONG).show();

        }

    }


    /**
     * This function I only used for debugging purposes to make sure
     * the layout auto-fits.
     * @param config
     */

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        //check for the rotation
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this,"LANDSCAPE",Toast.LENGTH_LONG).show();
          //  viewCreationCycle();

        } else if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "PORTRAIT", Toast.LENGTH_LONG).show();
            //viewCreationCycle();
        }

    }


    /*
     * Name: onCreateOptionsMenu
     * @return boolean
     * Description: Display settings menu
     *
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sort_results) {
            sortHelper(item);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sortHelper(MenuItem item) {
        //Sets the activity title to the new endpoint and toggles the menuItem title
        //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (item.getTitle().equals("Top Rated")) {


            getSupportActionBar().setTitle(R.string.top_rated_movies);
            //toolbar.setTitle("Top Rated Movies");
            item.setTitle(R.string.popular);
            mSortOrder = getResources().getString(R.string.media_top_rated);
        } else {
            getSupportActionBar().setTitle(R.string.popular_movies);
            item.setTitle(R.string.top_rated);
            //toolbar.setTitle("Popular Movies");
            mSortOrder = getResources().getString(R.string.media_popular);
        }

        network_error.setVisibility(View.GONE);
        viewCreationCycle();


    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        //Amend GridLayoutManager to autosize grid
        recyclerView.setLayoutManager(new GridAutofitLayoutManager(getApplicationContext(), Constants.columnWidth));

        mMovieAdapter = new MediumListRecyclerViewAdapter(mMediaInformation);
        recyclerView.setAdapter(mMovieAdapter);
    }

    /*
     * Name: getOnlineStatus
     * @return boolean - flag to indicate network status
     *  False:  Offline
     *  True:   Online
     * Description: Check on the device network status
     * Comment: Standard method on which to check the network availability
     *          Ensure required permissions have been added to Android.Manifest
     */

    public void getScreenDensity() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int density = metrics.densityDpi;


        if (density < Constants.DENSITY_280)
            mScreenDensity = getResources().getString(R.string.media_density_185);
        else if (density < Constants.DENSITY_480)
            mScreenDensity = getResources().getString(R.string.media_density_342);
        else if (density < Constants.DENSITY_570)
            mScreenDensity = getResources().getString(R.string.media_density_500);
        else
            mScreenDensity = getResources().getString(R.string.media_density_780);
    }



    /*
     * Name: onRequestMovieAPI
     * Comment: Access MovieAPI using the Volley library
     * Tasks:
     * 1. Check Network/Internet available
     * 2. Find "results" object and store it in an JSONArray
     * 3. Iterate through the JSONArray
     * 4. Read item contents and add to the movie array
     * 5. Send a dataset change notification to update the Adapter view
     * 6. Handle Exceptions
     *
     */

    private void onRequestMovieAPI() {

        final String MOVIE_API_URI = Constants.API_URI;

        final String MOVIE_API_KEY = Constants.API_QUERY + Constants.API_KEY;

        //  sorting based on two criteria:
        //  sort_by=popularity.desc/popular
        //  sort_by=vote_average.desc/top_rated

        // setting - "w92", "w154", "w185", "w342", "w500", "w780", or "original".
        // Added a setting to allow the user to select image size
        final String MOVIE_IMAGE_URI = Constants.IMAGE_URI;

        final JsonObjectRequest mJsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, MOVIE_API_URI + mSortOrder + MOVIE_API_KEY,
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Get the response array
                            JSONArray jsonArray = response.getJSONArray(Constants.jsonArrayResults);
                            String id;
                            String title;
                            double rating;
                            String poster_uri;

                            //Clear existing information
                            mMediaInformation.clear();


                            for (int i = 0; i < jsonArray.length(); i++) {
                                //Get the movie object
                                JSONObject movie = jsonArray.getJSONObject(i);

                                // Get the required details: ID + poster_path
                                id = movie.getString(Constants.jsonId);
                                title = movie.getString(Constants.jsonTitle);
                                rating = movie.getDouble(Constants.jsonVoteAverage);
                                poster_uri = movie.getString(Constants.jsonPosterPath);

                                //Add to movie structure
                                mMediaInformation.add(new Media(id,
                                        MOVIE_IMAGE_URI + mScreenDensity + poster_uri, title, rating));
                            }

                            //Notify a data set change
                            mMovieAdapter.notifyDataSetChanged();

                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.i("JSON", error.getMessage());
                    }
                });

        // Queue the async request
        Volley.newRequestQueue(getApplicationContext()).add(mJsonObjectRequest);
    }


    public class MediumListRecyclerViewAdapter
            extends RecyclerView.Adapter<MediumListRecyclerViewAdapter.ViewHolder> {

        private final ArrayList<Media> mMedia;

        public MediumListRecyclerViewAdapter(ArrayList<Media> media) {
            mMedia = media;
        }

        @Override
        public ListActivity.MediumListRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.medium_list_content, parent, false);
            return new ListActivity.MediumListRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ListActivity.MediumListRecyclerViewAdapter.ViewHolder holder, int position) {

            holder.mMediumTitle.setText(mMedia.get(position).getTitle());
            holder.mMediumRating.setText(getResources().getString(R.string.medium_fragment_rating_header) + String.valueOf(mMedia.get(position).getRating()));

            Picasso.get()
                    .load(mMedia.get(position).getThumbnail())
                    .placeholder(movie_placeholder)
                    .into(holder.mMediumPoster);


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Media Medium = mMediaInformation.get(holder.getLayoutPosition());

                    if (mTwoPane) {
                        Bundle arguments = new Bundle();

                        arguments.putString(Constants.ARG_MEDIUM_ID, Medium.getID());
                        arguments.putString(Constants.ARG_MEDIUM_TITLE, holder.mMediumTitle.getText().toString());

                        DetailFragment fragment = new DetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.medium_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, DetailActivity.class);

                        intent.putExtra(Constants.ARG_MEDIUM_ID, Medium.getID());
                        intent.putExtra(Constants.ARG_MEDIUM_TITLE, holder.mMediumTitle.getText().toString());
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mMedia.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mMediumTitle;
            public final TextView mMediumRating;
            public final ImageView mMediumPoster;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mMediumTitle = (TextView) view.findViewById(R.id.media_title);
                mMediumRating = (TextView) view.findViewById(R.id.media_rating);
                mMediumPoster = (ImageView) view.findViewById(R.id.media_image);
            }

        }
    }

}
