package com.example.resttrailer.model


import com.google.gson.annotations.SerializedName

class Movies(
    @SerializedName("page")
    val page: Int = 1,
    @SerializedName("results")
    val results: List<Result> = mutableListOf(),
    @SerializedName("total_pages")
    val totalPages: Int = 1,
    @SerializedName("total_results")
    val totalResults: Int = 1
)