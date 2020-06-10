package com.example.popularmoviesstage1.data;

import com.example.popularmoviesstage1.BuildConfig;
//import com.squareup.picasso.BuildConfig;

public class Constants {



    public static final String API_URI = "http://api.themoviedb.org/3/movie/" ;
    public static final String API_KEY = BuildConfig.TMDB_API_KEY;

    public static boolean isNetworkConnected = false;

    //setting - "w92", "w154", "w185", "w342", "w500", "w780", or "original".
    // Added a setting to allow the user to select image size
    public static final String IMAGE_URI = "http://image.tmdb.org/t/p/";
    public static final String API_QUERY = "?api_key=" ;
    public static final String jsonArrayResults = "results";
    public static final String jsonId = "id";
    public static final String jsonTitle = "title";
    public static final String jsonVoteAverage = "vote_average";
    public static final String jsonPosterPath = "poster_path";

    public static final String jsonResponseOriginalTitle ="original_title";
    public static final String jsonResponseReleaseDate ="release_date";
    public static final String jsonResponseOverview = "overview";
    public static final String jsonResponseRuntime ="runtime";
    public static final String jsonResponseMins = " mins";

     public static final String ARG_MEDIUM_ID        = "medium_id";
     public static final String ARG_MEDIUM_TITLE     = "medium_title";


    // Screen density settings
    public static final int DENSITY_280 = 280;
    public static final int DENSITY_480 = 480;
    public static final int DENSITY_570 = 570;

    //GridLayoutManager
    public static final int columnWidth = 342;









}
