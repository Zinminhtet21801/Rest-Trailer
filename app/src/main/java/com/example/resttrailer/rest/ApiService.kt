package com.example.resttrailer.rest

import com.example.resttrailer.constant.Constants.Companion.API_KEY
import com.example.resttrailer.detail.MovieDetail
import com.example.resttrailer.model.Movies
import com.example.resttrailer.model.Result
import com.example.resttrailer.video.Video
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

open interface ApiService {
    @GET("movie/popular")
    fun getPopular(@Query("api_key") apiKey: String = API_KEY): Call<Movies>

    @GET("movie/top_rated")
    fun getRecommended(@Query("api_key") apiKey: String = API_KEY): Call<Movies>

    @GET("search/movie")
    fun getSearch(
        @Query("query") query: String,
        @Query("api_key") apiKey: String = API_KEY
    ): Call<Movies>

    @GET("movie/upcoming")
    fun getUpcoming(@Query("api_key") apiKey: String = API_KEY): Call<Movies>

    @GET("movie/{movie_id}/videos")
    fun getVideo(@Path("movie_id") movie_id : Int, @Query("api_key") apiKey: String = API_KEY) : Call<Video>

    @GET("movie/{movie_id}")
    fun getMovieDetail(@Path("movie_id") movie_id: Int,@Query("api_key") apiKey: String = API_KEY) : Call<MovieDetail>
}