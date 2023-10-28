package app.unduit.a2achatapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.unduit.a2achatapp.databinding.ItemPropertyFeatureBinding

class PropertyFeaturesAdapter(
    val context: Context,
    private val list: ArrayList<String>
):  RecyclerView.Adapter<PropertyFeaturesAdapter.ViewHolder>(){

    inner class ViewHolder(val binding: ItemPropertyFeatureBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPropertyFeatureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.itemName.text = list[position]
    }
}