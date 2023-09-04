package app.unduit.a2achatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.models.Notification

class NotificationAdapter(
    private val onClickListener: (item: Notification, action: Int) -> Unit //Replace string notification object
): ListAdapter<Notification, NotificationAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
        //Initialize views
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return ViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]

        //set data
    }

    private class DiffCallback : DiffUtil.ItemCallback<Notification>() {

        override fun areItemsTheSame(oldItem: Notification, newItem: Notification) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification) =
            oldItem == newItem

    }

}