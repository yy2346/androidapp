package com.example.androidapptest2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.photo_layout.view.*

class PhotoAdapter (

    private val photos: MutableList<Photo> ) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>()

{

    public val photos1 = photos

    class PhotoViewHolder ( itemView: View): RecyclerView.ViewHolder(itemView)


    fun addId( photo: Photo){

        photos.add(photo)
        notifyItemInserted(photos.size - 1)

    }

    fun deleteId( ){

        photos.removeAll { photo ->

            photo.isChecked

        }

        notifyDataSetChanged()

    }

    fun clearSession( ) {
        photos.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {

        return PhotoViewHolder(

            LayoutInflater.from(parent.context).inflate(
                R.layout.photo_layout, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {

        var curPhoto = photos[position]
        holder.itemView.apply {

            tvPhoto.text = curPhoto.id
        }
    }

    override fun getItemCount(): Int {

        return photos.size
    }

}