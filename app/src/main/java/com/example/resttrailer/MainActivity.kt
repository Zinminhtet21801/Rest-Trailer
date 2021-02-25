package com.example.resttrailer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu

import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.resttrailer.constant.Constants
import com.example.resttrailer.model.Movies
import com.example.resttrailer.rest.RestClient
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.coroutines.*


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private var searchMovieName = "Harry Potter"
    private lateinit var moviePopularAdapter: MovieAdapter
    private lateinit var movieRecommendedAdapter: MovieAdapter
    private lateinit var movieRecentAdapter: MovieAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        moviePopularAdapter = MovieAdapter()
        movieRecentAdapter = MovieAdapter()
        movieRecommendedAdapter = MovieAdapter()
        rvPopular.apply {
            adapter = moviePopularAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        getPopular()

        rvUpcoming.apply {
            adapter = movieRecentAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        getUpcoming()

        rvRecommended.apply {
            adapter = movieRecommendedAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        getRecommended()

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val menuItem = menu?.findItem(R.id.app_bar_search)
        val searchView = menuItem?.actionView as SearchView
        searchView?.let {
            it.queryHint = "Search Movies"
            it.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchMovieName = query!!
                    getSearch(query ?: "Harry Potter")
                    menuItem.collapseActionView()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
//                    getSearch(newText ?: "Harry Potter")
                    return true
                }

            })
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun getSearch(movieName: String) {
        RestClient.getApiService().getSearch(movieName).enqueue(object : Callback<Movies> {
            override fun onResponse(call: Call<Movies>, response: Response<Movies>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val intent = Intent(this@MainActivity, SearchActivity::class.java)
                            .putExtra("searchMovieName", searchMovieName)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                    }
                }
            }

            override fun onFailure(call: Call<Movies>, t: Throwable) {

                Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()

            }
        })
    }

    private fun getPopular() {
        RestClient.getApiService().getPopular(Constants.API_KEY).enqueue(object : Callback<Movies> {
            override fun onResponse(call: Call<Movies>, response: Response<Movies>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        moviePopularAdapter.setNewData(it.results)
                    }
                }
            }

            override fun onFailure(call: Call<Movies>, t: Throwable) {
                Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun getRecommended() {
        RestClient.getApiService().getRecommended(Constants.API_KEY)
            .enqueue(object : Callback<Movies> {
                override fun onResponse(call: Call<Movies>, response: Response<Movies>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            movieRecommendedAdapter.setNewData(it.results)
                        }
                    }
                }

                override fun onFailure(call: Call<Movies>, t: Throwable) {
                    Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun getUpcoming() {
        RestClient.getApiService().getUpcoming(Constants.API_KEY).enqueue(object : Callback<Movies> {
            override fun onResponse(call: Call<Movies>, response: Response<Movies>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        movieRecentAdapter.setNewData(it.results)
                    }
                }
            }

            override fun onFailure(call: Call<Movies>, t: Throwable) {
                Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }


}