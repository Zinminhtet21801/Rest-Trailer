package com.example.resttrailer

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.resttrailer.model.Result
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.*
import kotlinx.android.synthetic.main.item_movies.view.*
import kotlinx.android.synthetic.main.item_movies.view.ivImageCover
import kotlin.reflect.typeOf

class MovieAdapter : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {
    private var movieList = mutableListOf<Result>()
    private lateinit var view : View


    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_movies, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {

        val item = movieList[position]
        holder.itemView.setOnLongClickListener { view : View ->
            Toast.makeText(view.context,item.title,Toast.LENGTH_SHORT).show()
            true
        }
        holder.itemView.setOnClickListener {
            val dialog = BottomSheetDialog(it.context)
            Log.d("context",it.context.toString())
            view = LayoutInflater.from(it.context).inflate(R.layout.bottom_sheet_dialog,null)
            view.tvMovieName.text = item.title
            view.tvOverview.text = item.overview
            Glide.with(holder.itemView)
                .load("https://image.tmdb.org/t/p/original/${item.posterPath}")
                .fallback(R.drawable.ic_image)
                .error(R.drawable.ic_image_broken)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view.ivImageCoverSheet)
            dialog.setContentView(view)
            dialog.show()
            view.setOnClickListener {
                it.context.startActivity(Intent(it.context,MovieActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("movieId",item.id.toString()))
            }
            view.ivCancel.setOnClickListener {
                dialog.dismiss()
            }

        }
        Glide.with(holder.itemView)
            .load("https://image.tmdb.org/t/p/original/${item.posterPath}")
            .fallback(R.drawable.ic_image)
            .error(R.drawable.ic_image_broken)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.itemView.ivImageCover)

    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    fun setNewData(list: List<Result>) {
        movieList.clear()
        movieList.addAll(list)
        notifyDataSetChanged()
    }
}