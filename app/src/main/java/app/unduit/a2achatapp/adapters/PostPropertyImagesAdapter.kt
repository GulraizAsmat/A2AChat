package app.unduit.a2achatapp.adapters

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.unduit.a2achatapp.R
import com.bumptech.glide.Glide

class PostPropertyImagesAdapter() : ListAdapter<Any, PostPropertyImagesAdapter.ViewHolder>(
    DiffCallback()
) {

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
        var imageView: ImageView = v.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.item_post_property_image, parent, false)
        return ViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = currentList[position]

        if(item is Uri) {
            holder.imageView.setImageURI(item)
        } else if(item is String) {
            Glide.with(holder.imageView).load(item).into(holder.imageView)
        }

    }

    private class DiffCallback : DiffUtil.ItemCallback<Any>() {

        override fun areItemsTheSame(oldItem: Any, newItem: Any) =
            oldItem == newItem

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Any, newItem: Any) =
            oldItem == newItem

    }


}