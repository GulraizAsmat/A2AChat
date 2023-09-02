package app.unduit.a2achatapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.listeners.AdapterListener

class HomeSwiperAdapter(
    val context: Context,
    private val listener: AdapterListener,
    private val homeSwipeList: ArrayList<Int>,
) :
    RecyclerView.Adapter<HomeSwiperAdapter.MyHolder>(), View.OnClickListener {


    inner class MyHolder(v: View) : RecyclerView.ViewHolder(v) {
        var view: View = v
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_card_swipe, parent, false)
        return MyHolder(view)
    }


    override fun getItemCount(): Int {
        // Return the number of cards in your stack

        return 10 /* number of cards */
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Initialize your card item views here
    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {

    }
}
