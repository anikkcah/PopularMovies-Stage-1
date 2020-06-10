package com.example.popularmoviesstage1;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.popularmoviesstage1.data.Constants;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import static com.example.popularmoviesstage1.R.drawable.movie_placeholder;

/**
 * A fragment representing a single Detail screen.
 * This fragment is either contained in a {@link ListActivity}
 * in two-pane mode (on tablets) or a {@link DetailActivity}
 * on handsets.
 */
public class DetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
//    public static final String ARG_ITEM_ID = "item_id";
    private static final String API_KEY  = Constants.API_KEY;
    private View mRootview;




    private String mID;
    private String mTitle;
    private String mRating;
    private String mPoster      = "";
    private String mYear        = "";
    private String mRuntime     = "";
    private String mThumbnail   = "";
    private String mOverview    = "";


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Add other parameters
        assert getArguments() != null;
        if (getArguments().containsKey(Constants.ARG_MEDIUM_TITLE)) {
            mTitle = getArguments().getString(Constants.ARG_MEDIUM_TITLE);

            Activity activity = this.getActivity();
            assert activity != null;
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mTitle);
            }
        }

        if (getArguments().containsKey(Constants.ARG_MEDIUM_ID)) {
            //Add ID to search against the Movie details
            mID = getArguments().getString(Constants.ARG_MEDIUM_ID);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootview = inflater.inflate(R.layout.medium_detail, container, false);

        if (mRootview != null) {
            // Make a call to the TMDB
            onRequestMovieAPI(mID);
        }

        return mRootview;
    }


    /*
     * @Name: getScreenDensity
     * @return void
     *      "w92", "w154", "w185", "w342", "w500", "w780", or "original".
     * @Description: Check the screen density
     */

    public String getScreenDensity() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int density = metrics.densityDpi;

        String strScreenDensity;

        if (density < Constants.DENSITY_280)
            strScreenDensity = getResources().getString(R.string.media_density_185);
        else if (density < Constants.DENSITY_480)
            strScreenDensity = getResources().getString(R.string.media_density_342);
        else if (density < Constants.DENSITY_570)
            strScreenDensity = getResources().getString(R.string.media_density_500);
        else
            strScreenDensity = getResources().getString(R.string.media_density_780);

        return strScreenDensity;
    }



       /*
     * Name: onRequestMovieAPI
     * Comment: Access MovieAPI using the Volley library
     * Tasks:
     * 1. Check Network/Internet available
     * 2. Find "results" object and store it in an JSONArray
     * 3. Iterate through the JSONArray
     * 4.   Read item contents and add to the movie array
     * 5. Send a data set change notification to update the Adapter view
     * 6. Handle Exceptions
     *
     */

    private void onRequestMovieAPI(String mID) {
        //mFilmAPI Query
        final String MOVIE_API_URI = getResources().getString(R.string.medium_tmdb_api);
        final String MOVIE_API_KEY = Constants.API_QUERY + API_KEY;
        String strQueryTMDB = MOVIE_API_URI+mID+MOVIE_API_KEY;




        // sort_by=popularity.desc/popular
        // sort_by=vote_average.desc/top_rated

        // Alter setting - "w92", "w154", "w185", "w342", "w500", "w780", or "original".
        // Add a setting to allow the user to select image size
        final String MOVIE_IMAGE_URI = getResources().getString(R.string.medium_image_uri);

    /*
     *  JSON Request - Volley JSON example
     */

        //Request movie information based on ID passed from MovieFragment

        final JsonObjectRequest mJsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, strQueryTMDB, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {


                            //Change these variables to local
                            mTitle = response.getString(Constants.jsonResponseOriginalTitle);
                            mYear = response.getString(Constants.jsonResponseReleaseDate);
                            mOverview = response.getString(Constants.jsonResponseOverview);
                            mRuntime = response.getString(Constants.jsonResponseRuntime) + Constants.jsonResponseMins;
                            mThumbnail = MOVIE_IMAGE_URI + getScreenDensity() + response.getString(Constants.jsonPosterPath);
                            mRating = response.getString(Constants.jsonVoteAverage) + "/10";




                            // populate the detail fragment
                            populateMovieDetails();

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
        Volley.newRequestQueue(getActivity()).add(mJsonObjectRequest);
    }


    /*
     * Populate the onscreen controls once the data has been downloaded
     */
    private void populateMovieDetails() {

        // Ensure the rootview is valid before accessing
        if (mRootview != null) {

            // Get reference to the UI controls
            ((TextView) mRootview.findViewById(R.id.medium_detail_title)).setText(mTitle);
            ((TextView) mRootview.findViewById(R.id.medium_detail_releasedt)).setText(mYear);
            ((TextView) mRootview.findViewById(R.id.medium_detail_plot)).setText(mOverview);
            ((TextView) mRootview.findViewById(R.id.medium_detail_rating)).setText(mRating);

            // Use Picasso to output an image - use placeholder where necessary
            Picasso.get()
                    .load(mThumbnail)
                    .placeholder(movie_placeholder)
                    .into(((ImageView) mRootview.findViewById(R.id.medium_detail_poster)));

        }
    }


}
