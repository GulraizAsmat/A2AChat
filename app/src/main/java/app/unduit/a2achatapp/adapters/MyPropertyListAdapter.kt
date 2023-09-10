package app.unduit.a2achatapp.adapters

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.databinding.ItemMyPropertyListBinding


import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.PropertyData

class MyPropertyListAdapter(
    val context: Context,
    private val listener: AdapterListener,
    private val propertyList: ArrayList<PropertyData>
) :
    RecyclerView.Adapter<MyPropertyListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: app.unduit.a2achatapp.databinding.ItemMyPropertyListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = app.unduit.a2achatapp.databinding.ItemMyPropertyListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
//        return propertyList.size // Return the number of items in your data list
        return 10 // Return the number of items in your data list
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val propertyItem = propertyList[position]

        // Bind your data to the layout using data binding


        // Set a click listener on the root view if needed
        holder.binding.menu.setOnClickListener {
//            listener.onItemClick(propertyItem)
            showPopupMenu(holder.binding.menu,position)
        }

        // Execute pending bindings to update the UI
        holder.binding.executePendingBindings()
    }

    private fun showPopupMenu(view: View, item: Int) {
        val popupWrapper = ContextThemeWrapper( view.context, R.style.PopupMenuStyle)
        val popupMenu = PopupMenu(popupWrapper, view)
        popupMenu.inflate(R.menu.property_menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_item1 -> {
                    // Handle the Edit action
                    // You can pass 'item' to the edit function or perform your edit operation here.
                    true
                }
                R.id.menu_item1 -> {
                    // Handle the Delete action
                    // You can pass 'item' to the delete function or perform your delete operation here.
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }


}
