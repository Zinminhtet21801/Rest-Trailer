package com.example.resttrailer

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.resttrailer.constant.Constants.Companion.API_KEY
import com.example.resttrailer.detail.MovieDetail
import com.example.resttrailer.rest.RestClient
import com.example.resttrailer.video.Video
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_movie.*
import kotlinx.android.synthetic.main.bottom_sheet_dialog.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MovieActivity : AppCompatActivity() {
    private var mPlayer: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var hlsUrl = "https://www.youtube.com/watch?v="
    private var movieId: Int = 0
    private var videoKey: String = ""
    private lateinit var movieGenre: StringBuilder
    private lateinit var dataSourceFactory: DataSource.Factory
    private var genreIndex = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)
        movieId = intent.getStringExtra("movieId")!!.toInt()
        getMovieDetail(movieId)
        getVideo(movieId)

    }

    @Suppress("DEPRECATION")
    private fun initPlayer() {
        mPlayer = SimpleExoPlayer.Builder(this).build()
        playerView.player = mPlayer
        mPlayer!!.playWhenReady = true
        mPlayer!!.seekTo(currentWindow, playbackPosition)
        mPlayer!!.setMediaSource(buildMediaSource())
        mPlayer!!.prepare()
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initPlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Util.SDK_INT >= 24 || mPlayer == null) {
            initPlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        playerView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    private fun releasePlayer() {
        if (mPlayer == null) {
            return
        }
        playWhenReady = mPlayer!!.playWhenReady
        playbackPosition = mPlayer!!.currentPosition
        currentWindow = mPlayer!!.currentWindowIndex
        mPlayer!!.release()
        mPlayer = null
    }

    @Suppress("DEPRECATION")
    private fun buildMediaSource(): MediaSource {

        dataSourceFactory = DefaultHttpDataSourceFactory()
        return HlsMediaSource.Factory(dataSourceFactory)
            .setAllowChunklessPreparation(true)
            .createMediaSource(MediaItem.fromUri(hlsUrl + videoKey))
    }

    private fun getVideo(movie: Int) {
        RestClient.getApiService().getVideo(movie, API_KEY).enqueue(object : Callback<Video> {
            override fun onResponse(call: Call<Video>, response: Response<Video>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        videoKey = it.results[0].key
                    }
                }
            }

            override fun onFailure(call: Call<Video>, t: Throwable) {
                Toast.makeText(this@MovieActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getMovieDetail(movie: Int) {
        RestClient.getApiService().getMovieDetail(movie, API_KEY)
            .enqueue(object : Callback<MovieDetail> {
                override fun onResponse(call: Call<MovieDetail>, response: Response<MovieDetail>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            movieGenre = StringBuilder()
                            for (genre in it.genres) {
                                if(it.genres.size > genreIndex) {
                                    movieGenre.append(genre.name + "/")
                                    genreIndex++
                                }else if (it.genres.size == genreIndex){
                                    movieGenre.append(genre.name)
                                }
                            }
                            genreIndex = 0
                            tvRating.text = "${it.voteAverage}/10"
                            tvGenre.text = movieGenre
                            movieGenre.clear()
                            Log.d("tvOverview",it.overview)
                            tvContentDescription.text = it.overview
                        }
                    }
                }

                override fun onFailure(call: Call<MovieDetail>, t: Throwable) {
                    Toast.makeText(this@MovieActivity,t.message,Toast.LENGTH_SHORT).show()
                }
            })
    }
}