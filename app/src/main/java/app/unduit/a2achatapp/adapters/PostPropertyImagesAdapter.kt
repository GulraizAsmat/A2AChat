package app.unduit.a2achatapp.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.unduit.a2achatapp.R

class PostPropertyImagesAdapter() : ListAdapter<Uri, PostPropertyImagesAdapter.ViewHolder>(
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

        holder.imageView.setImageURI(item)

    }

    private class DiffCallback : DiffUtil.ItemCallback<Uri>() {

        override fun areItemsTheSame(oldItem: Uri, newItem: Uri) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Uri, newItem: Uri) =
            oldItem == newItem

    }


}